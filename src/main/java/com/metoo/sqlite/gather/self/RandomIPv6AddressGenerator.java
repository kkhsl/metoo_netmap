package com.metoo.sqlite.gather.self;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class RandomIPv6AddressGenerator {


    public static void main(String[] args) {
        try {
            String networkSegment = "240b:8160:3008:700::/64";
            int numberOfAddresses = 10; // Specify the number of addresses to generate

            generateRandomIPv6Addresses(networkSegment, numberOfAddresses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> generateRandomIPv6Addresses(String networkSegment, int count) throws UnknownHostException {
        // Extract the network base address and prefix length
        String[] parts = networkSegment.split("/");
        String baseAddress = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        // Convert base address to a BigInteger
        BigInteger base = ipToBigInteger(baseAddress);

        // Calculate the range for the offset
        int offsetBits = 128 - prefixLength;
        BigInteger maxOffset = BigInteger.ONE.shiftLeft(offsetBits).subtract(BigInteger.ONE);

        List<String> ipv6List = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < count; i++) {
            // Generate a random offset
            BigInteger offset = new BigInteger(offsetBits, random).and(maxOffset);

            // Apply offset to base address
            BigInteger address = base.add(offset);

            // Convert the resulting BigInteger back to an IPv6 address
            String addressString = bigIntegerToIPv6(address);

//            System.out.println("Generated Address: " + addressString);

            ipv6List.add(addressString);
        }
        return ipv6List;
    }

    private static void generateRandomIPv6Addresses2(String networkSegment, int count) throws UnknownHostException {
        // Extract the network base address and prefix length
        String[] parts = networkSegment.split("/");
        String baseAddress = parts[0];
        int prefixLength = Integer.parseInt(parts[1]);

        // Convert base address to a BigInteger
        BigInteger base = ipToBigInteger(baseAddress);

        // Calculate the range for the offset
        int offsetBits = 128 - prefixLength;
        BigInteger maxOffset = BigInteger.ONE.shiftLeft(offsetBits).subtract(BigInteger.ONE);

        SecureRandom random = new SecureRandom();
        for (int i = 0; i < count; i++) {
            // Generate a random offset
            BigInteger offset = new BigInteger(offsetBits, random).and(maxOffset);

            // Apply offset to base address
            BigInteger address = base.add(offset);

            // Convert the resulting BigInteger back to an IPv6 address
            String addressString = bigIntegerToIPv6(address);
            System.out.println("Generated Address: " + addressString);
        }
    }

    private static BigInteger ipToBigInteger(String ip) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ip);
        byte[] bytes = inetAddress.getAddress();
        // Ensure the byte array is 16 bytes long (for IPv6)
        if (bytes.length < 16) {
            byte[] ipv6Bytes = new byte[16];
            System.arraycopy(bytes, 0, ipv6Bytes, 16 - bytes.length, bytes.length);
            bytes = ipv6Bytes;
        }
        return new BigInteger(1, bytes);
    }

    private static String bigIntegerToIPv6(BigInteger bigInteger) {
        byte[] bytes = bigInteger.toByteArray();
        // Ensure the byte array is 16 bytes long (for IPv6)
        if (bytes.length < 16) {
            byte[] paddedBytes = new byte[16];
            System.arraycopy(bytes, 0, paddedBytes, 16 - bytes.length, bytes.length);
            bytes = paddedBytes;
        }
        try {
            InetAddress inetAddress = InetAddress.getByAddress(bytes);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
