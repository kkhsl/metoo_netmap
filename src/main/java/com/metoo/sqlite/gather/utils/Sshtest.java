package com.metoo.sqlite.gather.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.metoo.sqlite.utils.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-23 23:07
 */
public class Sshtest {

    public static void main(String[] args) {
        String[] params = {"h3c", "switch", "192.168.100.1", "ssh", "22", "metoo", "metoo89745000",
                "aliveint"};
        System.out.println(exec("/opt/sqlite/script/main.py", params));;
    }

    public static String  exec(String path, String[] params){
        String host = "192.168.5.101";
        int port = 22;
        String username = "root";
        String password = "metoo89745000";

        Session session = null;

        // 创建连接
        Connection conn = new Connection(host, port);
        // 启动连接
        try {
            conn.connect();
            // 验证用户密码
            conn.authenticateWithPassword(username, password);
            session = conn.openSession();
            String py_version = "python3";

//            if ("dev".equals(env)) {
//                py_version = "python";
//            }

            String[] args = new String[]{
                    py_version, path};

            if (params.length > 0) {
                String[] mergedArray = new String[args.length + params.length];

                int argsLen = args.length;

                for (int i = 0; i < mergedArray.length; i++) {

                    if (i < argsLen) {
                        mergedArray[i] = args[i];
                    } else {
                        mergedArray[i] = params[i - argsLen];
                    }
                }
                try {
                    StringBuilder sb = new StringBuilder();
                    for (String str : mergedArray) {
                        sb.append(str).append(" ");
                    }
                    session.execCommand(sb.toString().trim());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    StringBuilder sb = new StringBuilder();
                    for (String str : args) {
                        sb.append(str).append(" ");
                    }
                    session.execCommand(sb.toString().trim());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 消费所有输入流
            String inStr = consumeInputStream(session.getStdout());
            return inStr;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(session != null){
                session.close();
            }
            if(conn != null){
                conn.close();
            }
        }
        return "";
    }

    public static String consumeInputStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s ;
        StringBuilder sb = new StringBuilder();
        while((s=br.readLine())!=null){
            sb.append(s);
        }
        return sb.toString();
    }




    @Test
    public void test() throws IOException {
        String host = "192.168.5.205";
        int port = 22;
        String username = "nrsm";
        String password = "metoo89745000";
        // 创建连接
        Connection conn = new Connection(host, port);
        // 启动连接
        conn.connect();
        // 验证用户密码
        conn.authenticateWithPassword(username, password);

        Session session = conn.openSession();

        session.execCommand("python3 /opt/nrsm/py/gethostname.py 1.2.3.9 v2c public@123");

        // 消费所有输入流
        String inStr = consumeInputStream(session.getStdout());
        String errStr = consumeInputStream(session.getStderr());

        System.out.println(inStr);
        if(StringUtils.isNotEmpty(inStr)){
            System.out.println(1);
        }else{
            System.out.println(2);
        }

        session.close();
        conn.close();
    }
}
