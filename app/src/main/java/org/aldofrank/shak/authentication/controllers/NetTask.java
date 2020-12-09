package org.aldofrank.shak.authentication.controllers;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetTask extends AsyncTask<String, Integer, String>
{
    @Override
    protected String doInBackground(String... params)
    {
        InetAddress addr = null;
        try
        {
            String host = params[0];
            addr = InetAddress.getByName(host);
        }

        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        try {
            return String.valueOf(addr.isReachable(3000));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "no";
        //return addr.getHostAddress();
    }
}