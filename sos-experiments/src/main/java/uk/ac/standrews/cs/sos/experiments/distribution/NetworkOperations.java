package uk.ac.standrews.cs.sos.experiments.distribution;

import com.jcraft.jsch.*;
import uk.ac.standrews.cs.guid.ALGORITHM;
import uk.ac.standrews.cs.guid.GUIDFactory;
import uk.ac.standrews.cs.guid.exceptions.GUIDGenerationException;
import uk.ac.standrews.cs.sos.experiments.ExperimentConfiguration;

import java.io.*;
import java.util.concurrent.*;

import static uk.ac.standrews.cs.sos.experiments.Constants.*;

// Based on the following examples:
// http://www.jcraft.com/jsch/examples/ScpTo.java.html
// http://www.jcraft.com/jsch/examples/ScpFrom.java.html
public class NetworkOperations implements Closeable {

    private static final String EXEC_CHANNEL = "exec";
    private static final int PROCESS_KILL_SIGNAL = 15; // Signal name: TERM
    private static final int BUFFER_SIZE = 2*8096;

    private ExperimentConfiguration.Experiment.Node.SSH ssh;
    private Session session;

    public void setSsh(ExperimentConfiguration.Experiment.Node.SSH ssh) {
        this.ssh = ssh;
    }

    /**
     * Connect to the remote host via SSH
     *
     * @throws NetworkException if unable to connect to node
     */
    public void connect() throws NetworkException {

        try {
            JSch jsch = new JSch();
            if (ssh.getKnown_hosts() != null && !ssh.getKnown_hosts().isEmpty() && new File(ssh.getKnown_hosts()).exists()) {
                jsch.setKnownHosts(ssh.getKnown_hosts());
            }

            if (ssh.getConfig() != null && !ssh.getConfig().isEmpty() && new File(ssh.getConfig()).exists()) {
                ConfigRepository configRepository = com.jcraft.jsch.OpenSSHConfig.parseFile(ssh.getConfig());
                jsch.setConfigRepository(configRepository);
            }

            if (ssh.getType() == 1) {
                jsch.addIdentity(ssh.getPrivateKeyPath(), ssh.getPassphrase());
            }

            session = jsch.getSession(ssh.getUser(), ssh.getHost(), 22);
            session.setConfig("StrictHostKeyChecking", "no");

            if (ssh.getType() == 0) {
                session.setPassword(ssh.getPassword());
            }

            session.connect();

        } catch (JSchException | IOException e) {
            e.printStackTrace();
            throw new NetworkException();
        }
    }

    @Override
    public void close() throws IOException {

        if (session != null) {
            session.disconnect();
        }
    }

    public void sendFile(String lfile, String rfile, boolean checkRemoteFile) throws NetworkException {
        System.out.println("NETWORK - Sending file " + lfile + " to the host " + ssh.getHost() + " in path " + rfile);

        boolean sent = false;
        int trial = 0;
        while(trial < 3) {

            try {
                sendFileToRemote(lfile, rfile, checkRemoteFile);
                sent = true;
                break;
            } catch (NetworkException e) {

                System.err.println("Unable to send file. Try again...?");
            }
            trial++;
        }

        if (!sent) {
            throw new NetworkException("Unable to send file " + lfile + " to the host " + ssh.getHost() + " in path " + rfile + " after 3 trials");
        }

    }

    /**
     * Send a local file to a remote location
     *
     * @param lfile
     * @param rfile
     * @throws NetworkException
     */
    private void sendFileToRemote(String lfile, String rfile, boolean checkRemoteFile) throws NetworkException {

        if (!new File(lfile).exists()) {
            System.out.println("The local file " + lfile + " does not exist.");
            throw new NetworkException();
        }

        if (checkRemoteFile) {
            try (InputStream inputStream = new FileInputStream(new File(lfile))){

                // MD5 should be good enough for this purpose
                String lCheckum = GUIDFactory.generateGUID(ALGORITHM.MD5, inputStream).toString();
                String rChecksum = checkSum("md5sum", rfile);

                if (lCheckum.equals(rChecksum)) {
                    System.out.println("Remote file " + rfile + " and local file " + lfile + " have the same MD5 checksum " + lCheckum);
                    System.out.println("File will not be sent to the remote node");
                    return;
                }
            } catch (IOException | GUIDGenerationException e) {
                throw new NetworkException();
            }
        }

        try {
            // exec 'scp -t rfile' remotely
            boolean ptimestamp = false; // https://stackoverflow.com/questions/22226440/mtime-sec-is-not-present
            String command = "scp " + (ptimestamp ? "-p" : "") + " -t \"" + rfile + "\"";
            send(command, lfile, ptimestamp);

        } catch (JSchException | IOException e) {
            throw new NetworkException();
        }
    }

    public void sendDirectory(String lDirectory, String rDirectory, boolean checkRemoteFiles) throws NetworkException {

        makePath(rDirectory);

        File[] files = new File(lDirectory).listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Send sub-directory: " + file.getName());
                sendDirectory(file.getAbsolutePath(), rDirectory + "/" + file.getName(), checkRemoteFiles);
            } else {
                if (file.getName().startsWith(".")) continue; // Do not transfer hidden files

                sendFile(file.getAbsolutePath(), rDirectory + "/" + file.getName(), checkRemoteFiles);
            }
        }
    }

    /**
     * Get a file from a remote location
     *
     * @param rfile remote file path
     * @param lfile destination on local disk
     * @throws NetworkException
     */
    public void getFile(String rfile, String lfile) throws NetworkException {

        try {
            // exec 'scp -f rfile' remotely
            String command = "scp -f \"" + rfile + "\"";
            receive(command, lfile);

        } catch (JSchException | IOException e) {
            throw new NetworkException();
        }
    }

    public void deleteFile(String rfile) throws NetworkException {

        try {
            String command = "rm -f \"" + rfile + "\"";
            exec(command);

        } catch (JSchException | IOException e) {
            throw new NetworkException();
        }
    }

    public void deleteFolder(String rFolder) throws NetworkException {

        try {
            String command = "rm -rf \"" + rFolder + "\"";
            exec(command);

        } catch (JSchException | IOException e) {
            throw new NetworkException();
        }
    }

    /**
     * Execute the Jar at the remote location
     *
     * @param basePath
     * @param jarPath
     * @param args
     * @param outFile
     * @param pidFile
     * @throws NetworkException
     */
    public void executeJar(String basePath, String java, String jarPath, String args, String outFile, String pidFile) throws NetworkException {
        System.out.println("NETWORK - Executing jar file " + jarPath);

        try {
            String command = "cd " + basePath + "; " + java +
                    " -Xms" + JVM_INITIAL_HEAP_SIZE_IN_GB + "g" +
                    " -Xmx" + JVM_MAX_HEAP_SIZE_IN_GB + "g" +
                    // Garbage Collect G1
                    " -XX:+UseG1GC -XX:MaxGCPauseMillis=" + MAX_GB_PAUSE_MILLIS + // https://stackoverflow.com/questions/7980177/agressive-garbage-collector-strategy
                    " -XX:G1HeapRegionSize=4" +
                    " -XX:ParallelGCThreads=8 -XX:ConcGCThreads=2 " +
                    " -XX:+UseStringDeduplication " + // Java 8.20 optimisation, https://blog.takipi.com/garbage-collectors-serial-vs-parallel-vs-cms-vs-the-g1-and-whats-new-in-java-8/
                    " -Djava.awt.headless=true -jar " + jarPath + " " + args + "  > " + outFile + " 2>&1 & echo $! > " + pidFile;
            exec(command);

        } catch (JSchException | IOException e) {
            throw new NetworkException();
        }
    }

    public void scheduleJar() {
        // TODO - crontab cronfile
    }

    public void unscheduleJar() {
        // TODO - crontab -r
    }

    public void killProcess(String pidFile) throws NetworkException {
        System.out.println("NETWORK - Killing process with pidfile " + pidFile);

        try {
            String command = "kill -" + PROCESS_KILL_SIGNAL + " `cat " + pidFile + "`";
            exec(command);

        } catch (JSchException | IOException e) {
            throw new NetworkException();
        }
    }

    public String checkSum(String hashprogram, String rfile) throws NetworkException {
        System.out.println("NETWORK - Remote check sum for remote file " + rfile);

        try {
            String command = hashprogram + " \"" + rfile + "\"";
            String checksum = exec(command);

            if (checksum != null && !checksum.isEmpty()) {
                return checksum.split(" ")[0];
            } else {
                return "";
            }

        } catch (JSchException | IOException e) {
            throw new NetworkException();
        }

    }

    public void makePath(String path) throws NetworkException {
        System.out.println("NETWORK - Making remote path " + path);

        try {
            String command = "mkdir -p \"" + path + "\"";
            exec(command);

        } catch (JSchException | IOException e) {
            throw new NetworkException();
        }
    }

    private void send(String command, String lfile, boolean ptimestamp) throws IOException, JSchException {
        Channel channel = session.openChannel(EXEC_CHANNEL);
        ((ChannelExec) channel).setCommand(command);

        channel.connect();

        try (OutputStream out=channel.getOutputStream();
             InputStream in=channel.getInputStream()) {

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Callable<Boolean> task = () -> {
                try {
                    if (checkAck(in) != 0) {
                        return false;
                    }
                } catch (IOException e) {
                    return false;
                }

                return true;
            };

            Future<Boolean> future = executor.submit(task);
            boolean valid;
            try {
                valid = future.get(10, TimeUnit.SECONDS);
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                // handle the timeout
                e.printStackTrace();
                throw new JSchException();
            } finally {
                future.cancel(true); // may or may not desire this
            }

            if (!valid) {
                throw new JSchException();
            }

            File _lfile = new File(lfile);
            String internalCommand;
            if (ptimestamp) {
                internalCommand = "T " + (_lfile.lastModified() / 1000) + " 0";
                // The access time should be sent here,
                // but it is not accessible with JavaAPI ;-<
                internalCommand += (" " + (_lfile.lastModified() / 1000) + " 0\n");
                out.write(internalCommand.getBytes());
                out.flush();
                if (checkAck(in) != 0) {
                    throw new JSchException();
                }
            }

            // send "C0644 filesize filename", where filename should not include '/'
            long filesize = _lfile.length();
            System.out.println("File " + lfile + " with size " + filesize / 1000000.0 + " MB will be transferred via SSH to remote node");

            internalCommand = "C0644 " + filesize + " ";
            if (lfile.lastIndexOf('/') > 0) {
                internalCommand += lfile.substring(lfile.lastIndexOf('/') + 1);
            } else {
                internalCommand += lfile;
            }
            internalCommand += "\n";
            out.write(internalCommand.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                throw new JSchException();
            }

            // send a content of lfile
            try (FileInputStream fis = new FileInputStream(lfile)) {
                byte[] buf = new byte[BUFFER_SIZE];
                long dataSent = 0;
                int printingIndex = 0, printingFrequency = 1000; // This is just an arbitrary number to avoid too much printing

                while (true) {
                    int len = fis.read(buf, 0, buf.length);
                    if (len <= 0) break;

                    dataSent += len;
                    if (printingIndex % printingFrequency == 0) {
                        String outputToConsole = "Sent " + dataSent / 1000000.0 + " / " + filesize / 1000000.0 + " MB";
                        System.out.println(outputToConsole);
                    }
                    printingIndex++;

                    out.write(buf, 0, len); //out.flush();
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
            }

            if (checkAck(in) != 0) {
                throw new JSchException();
            } else {
                System.out.println("Content sent successfully");
            }

        }

        channel.disconnect();
    }

    private void receive(String command, String lfile) throws IOException, JSchException {

        Channel channel = session.openChannel(EXEC_CHANNEL);
        ((ChannelExec) channel).setCommand(command);

        channel.connect();

        String prefix=null;
        if(new File(lfile).isDirectory()){
            prefix=lfile+File.separator;
        }

        try (OutputStream out=channel.getOutputStream();
             InputStream in=channel.getInputStream()) {
            byte[] buf = new byte[BUFFER_SIZE];

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();

            while (true) {
                int c = checkAck(in);
                if (c != 'C') {
                    break;
                }

                // read '0644 '
                in.read(buf, 0, 5);

                long filesize = 0L;
                while (true) {
                    if (in.read(buf, 0, 1) < 0) {
                        // error
                        break;
                    }
                    if (buf[0] == ' ') break;
                    filesize = filesize * 10L + (long) (buf[0] - '0');
                }

                String file;
                for (int i = 0; ; i++) {
                    in.read(buf, i, 1);
                    if (buf[i] == (byte) 0x0a) {
                        file = new String(buf, 0, i);
                        break;
                    }
                }

                //System.out.println("filesize="+filesize+", file="+file);

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();

                // read a content of lfile
                try (FileOutputStream fos = new FileOutputStream(prefix == null ? lfile : prefix + file)) {
                    int foo;
                    while (true) {
                        if (buf.length < filesize) foo = buf.length;
                        else foo = (int) filesize;
                        foo = in.read(buf, 0, foo);
                        if (foo < 0) {
                            // error
                            break;
                        }
                        fos.write(buf, 0, foo);
                        filesize -= foo;
                        if (filesize == 0L) break;
                    }
                }

                if (checkAck(in) != 0) {
                    System.exit(0);
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
            }
        }

        channel.disconnect();
    }

    private String exec(String command) throws IOException, JSchException {
        System.out.println("Executing command: " + command);

        Channel channel = session.openChannel(EXEC_CHANNEL);
        ((ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((ChannelExec)channel).setErrStream(System.err);
        InputStream in=channel.getInputStream();

        channel.connect();

        String retval = "";

        byte[] tmp=new byte[BUFFER_SIZE];
        while(true){
            while(in.available()>0){
                int i=in.read(tmp, 0, BUFFER_SIZE);
                if(i<0)break;

                String out = new String(tmp, 0, i);
                retval += out;
                System.out.print(out);
            }
            if(channel.isClosed()){
                if(in.available()>0) continue;
                System.out.println("exit-status: "+channel.getExitStatus());
                break;
            }
            try{Thread.sleep(1000);}catch(Exception ee){}
        }

        channel.disconnect();

        return retval;
    }

    private int checkAck(InputStream in) throws IOException {

        int b=in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if(b==0) return b;
        if(b==-1) return b;
        if(b==1 || b==2){
            StringBuilder sb=new StringBuilder();
            int c;
            do {
                c=in.read();
                sb.append((char)c);
            }
            while(c!='\n');
            if(b==1){ // error
                System.out.print(sb.toString());
            }
            if(b==2){ // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

}