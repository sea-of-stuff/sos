package uk.ac.standrews.cs.sos.experiments.distribution;

import com.jcraft.jsch.*;

import java.io.*;

// Based on the following examples:
// http://www.jcraft.com/jsch/examples/ScpTo.java.html
// http://www.jcraft.com/jsch/examples/ScpFrom.java.html
public class NetworkOperations {

    public String privateKeyPath;
    public String passphrase;
    public String knownHostsPath = "/Users/sic2/.ssh/known_hosts";
    public String user;
    public String host;

    private Session session;

    /**
     * Connect to the remote host via SSH
     *
     * @throws NetworkException
     */
    public void connect() throws NetworkException {

        try {
            JSch jsch = new JSch();
            jsch.addIdentity(privateKeyPath, passphrase);
            jsch.setKnownHosts(knownHostsPath);

            session = jsch.getSession(user, host, 22);
            session.connect();

        } catch (JSchException e) {
            throw new NetworkException();
        }
    }

    /**
     * Disconnect from the remote host
     */
    public void disconnect() {

        session.disconnect();
    }

    /**
     * Send a local file to a remote location
     *
     * @param lfile
     * @param rfile
     * @throws NetworkException
     */
    public void sendFile(String lfile, String rfile) throws NetworkException {

        try {
            // exec 'scp -t rfile' remotely
            boolean ptimestamp = false; // https://stackoverflow.com/questions/22226440/mtime-sec-is-not-present
            String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + rfile;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.connect();

            send(channel, lfile, ptimestamp);

            channel.disconnect();

        } catch (JSchException | IOException e) {
            throw new NetworkException();
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
            String command = "scp -f " + rfile;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.connect();

            receive(channel, lfile);

            channel.disconnect();
        } catch (JSchException | IOException e) {
            throw new NetworkException();
        }
    }

    /**
     * Execute the Jar at the remote location
     *
     * @param jarPath
     * @throws JSchException
     */
    public void executeJar(String jarPath, String args) throws NetworkException {

        try {
            String command = "java -jar " + jarPath + " " + args;

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            channel.disconnect();

        } catch (JSchException e) {
            throw new NetworkException();
        }
    }

    private void send(Channel channel, String lfile, boolean ptimestamp) throws IOException {

        try (OutputStream out=channel.getOutputStream();
            InputStream in=channel.getInputStream()) {

            if (checkAck(in) != 0) {
                System.exit(0);
            }


            File _lfile = new File(lfile);
            String command;
            if (ptimestamp) {
                command = "T " + (_lfile.lastModified() / 1000) + " 0";
                // The access time should be sent here,
                // but it is not accessible with JavaAPI ;-<
                command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
                out.write(command.getBytes());
                out.flush();
                if (checkAck(in) != 0) {
                    System.exit(0);
                }
            }

            // send "C0644 filesize filename", where filename should not include '/'
            long filesize = _lfile.length();
            command = "C0644 " + filesize + " ";
            if (lfile.lastIndexOf('/') > 0) {
                command += lfile.substring(lfile.lastIndexOf('/') + 1);
            } else {
                command += lfile;
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                System.exit(0);
            }

            // send a content of lfile
            FileInputStream fis = new FileInputStream(lfile);
            byte[] buf = new byte[1024];
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0) break;
                out.write(buf, 0, len); //out.flush();
            }
            fis.close();
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            if (checkAck(in) != 0) {
                System.exit(0);
            } else {
                System.out.println("Content sent successfully");
            }

        }
    }

    private void receive(Channel channel, String lfile) throws IOException {

        String prefix=null;
        if(new File(lfile).isDirectory()){
            prefix=lfile+File.separator;
        }

        try (OutputStream out=channel.getOutputStream();
             InputStream in=channel.getInputStream()) {
            byte[] buf = new byte[1024];

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

                String file = null;
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
                FileOutputStream fos = new FileOutputStream(prefix == null ? lfile : prefix + file);
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
                fos.close();

                if (checkAck(in) != 0) {
                    System.exit(0);
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
            }
        }
    }

    private int checkAck(InputStream in) throws IOException{
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