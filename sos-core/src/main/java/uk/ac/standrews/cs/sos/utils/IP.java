package uk.ac.standrews.cs.sos.utils;

import java.net.*;
import java.util.Enumeration;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class IP {

    // https://stackoverflow.com/a/9482369/2467938
    /*
        - Any address in the range 127.xxx.xxx.xxx is a "loopback" address. It is only visible to "this" host.
        - Any address in the range 192.168.xxx.xxx is a private (aka site local) IP address. These are reserved for use within an organization.
            The same applies to 10.xxx.xxx.xxx addresses, and 172.16.xxx.xxx through 172.31.xxx.xxx.
        - Addresses in the range 169.254.xxx.xxx are link local IP addresses. These are reserved for use on a single network segment.
        - Addresses in the range 224.xxx.xxx.xxx through 239.xxx.xxx.xxx are multicast addresses.
        - The address 255.255.255.255 is the broadcast address.
        - Anything else should be a valid public point-to-point IPv4 address.
     */
    public static InetAddress findLocalAddress() {

        try {
            InetAddress local = InetAddress.getLocalHost();

            if (!isLoopback(local)) {
                return local;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            Enumeration<NetworkInterface> interfaceEnumeration =  NetworkInterface.getNetworkInterfaces();
            while(interfaceEnumeration.hasMoreElements()) {

                NetworkInterface networkInterface = interfaceEnumeration.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while(inetAddresses.hasMoreElements()) {

                    InetAddress address = inetAddresses.nextElement();
                    if (isIPV4(address) && !isLoopback(address) && !isPrivate(address) &&
                            !isLinkLocal(address) && !isMulticast(address) && !isBroadcast(address)) {
                        return address;
                    }

                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // NOTHING WAS FOUND
        return null;

    }

    private static boolean isLoopback(InetAddress address) {

        return address.getHostAddress().startsWith("127.");
    }

    private static boolean isPrivate(InetAddress address) {

        String ip = address.getHostAddress();
        return ip.startsWith("198.168.") || ip.startsWith("10.") || ip.startsWith("172.16.") || ip.startsWith("172.31.");
    }

    private static boolean isLinkLocal(InetAddress address) {

        return address.getHostAddress().startsWith("169.254.");
    }

    private static boolean isMulticast(InetAddress address) {

        String ip = address.getHostAddress();
        return ip.startsWith("224.") || ip.startsWith("239.");
    }

    private static boolean isBroadcast(InetAddress address) {

        return address.getHostAddress().equals("255.255.255.255");
    }

    private static boolean isIPV4(InetAddress address) {
        return address instanceof Inet4Address;
    }
}
