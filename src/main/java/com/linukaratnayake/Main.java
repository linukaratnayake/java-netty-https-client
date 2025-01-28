package com.linukaratnayake;

import com.linukaratnayake.netty.NettyHttpsClient;
import java.util.Properties;

import java.net.URISyntaxException;

public class Main {
    public static void main (String[] args) {
        Properties props = System.getProperties();
        props.setProperty("jdk.tls.namedGroups", "X25519Kyber768Draft00,x25519,secp256r1,secp384r1,secp521r1");

        String host = "www.google.com";
        int port = 443;
        String protocol = "https";

        try {
            NettyHttpsClient nettyHttpsClient = new NettyHttpsClient(host, port, protocol);
            nettyHttpsClient.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
