package com.linukaratnayake;

import com.linukaratnayake.netty.NettyHttpClient;

import java.net.URISyntaxException;

public class Main {
    public static void main (String[] args) {
        System.out.println("Hello");

        String host = "www.google.com";
        int port = 80;
        String protocol = "http://";

        try {
            NettyHttpClient nettyHttpClient = new NettyHttpClient(host, port, protocol);
            nettyHttpClient.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
