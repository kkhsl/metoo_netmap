package com.metoo.sqlite.gather.Process;

import com.metoo.sqlite.gather.common.PyCommand;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-24 11:40
 */
@Slf4j
@Component
public class PythonScriptRunner {

    @Test
    public void test(){
        PyCommand pyCommand = new PyCommand();
        pyCommand.setName("main.py");
        pyCommand.setParams(new String[]{"h3c", "switch", "192.168.100.1", "ssh", "22", "metoo", "metoo89745000", "aliveint"});
        List command = pyCommand.toArray();

        String result = this.runPythonScript(command);

        System.out.println(result);;
    }

    public String runPythonScript(String... args) {
        try {
            // 构建命令行参数，包括 Python 解释器和脚本路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.redirectErrorStream(true);
            pb.directory(new File("/opt/sqlite/script/"));

            // 将参数添加到命令行参数中
            pb.command().addAll(Arrays.asList(args));

            // 启动进程并等待其完成
            Process process = pb.start();
            process.waitFor();

            // 读取进程输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.lines().collect(Collectors.joining("\n"));

            // 关闭输入流
            reader.close();

            // 返回执行结果
            log.info("执行结果:"+ output);
            return output;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ""; // 或者抛出异常，根据需求处理
        }
    }

    public String runPythonScript(List<String> params) {
        try {
            // 构建命令行参数，包括 Python 解释器和脚本路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.redirectErrorStream(true);

            // 将参数添加到命令行参数中
            pb.command().addAll(params);

            // 启动进程并等待其完成
            Process process = pb.start();
            process.waitFor();

            // 读取进程输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.lines().collect(Collectors.joining("\n"));

            // 关闭输入流
            reader.close();

            // 返回执行结果
            return output;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ""; // 或者抛出异常，根据需求处理
        }
    }

    public static void main(String[] args) {

        // python3 /opt/sqlite/script/main.py h3c switch 192.168.100.1 ssh 22 metoo metoo89745000 aliveint

        PythonScriptRunner runner = new PythonScriptRunner();

        String result = runner.runPythonScript("python3","/opt/sqlite/script/main.py",
                "h3c", "switch", "192.168.100.1",
                "ssh", "22", "metoo", "metoo89745000", "aliveint");

        System.out.println("Python script output:\n" + result);
    }

//    public String exec(String scriptPath, String... args) {
//        log.info("==============arg " + args);
//        try {
//            // 构建命令行参数，包括 Python 解释器和脚本路径
//            ProcessBuilder pb = new ProcessBuilder();
//            pb.redirectErrorStream(true);
//
//            // 设置工作目录
//            pb.directory(new File(scriptPath));
//
//            // 将参数添加到命令行参数中
//            pb.command().addAll(Arrays.asList(args));
//
//            // 启动进程并等待其完成
//            log.info("==============arg 7");
//            Process process = pb.start();
//
//            log.info("==============arg 8");
//            // 等待进程终止，并获取退出值
//            int exitCode = process.waitFor();
//            System.out.println("==================Exited with code: " + exitCode);
//
//            log.info("==============arg 1");
//            // 读取进程输出
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
//
//            log.info("==============arg 2");
////            String output = reader.lines().toString();
//            String output = reader.lines().collect(Collectors.joining("\n"));
//
//            log.info("==============arg 3");
//            // 关闭输入流
//            reader.close();
//
//            log.info("==============arg 4");
//            // 返回执行结果
//            log.info("==========================result:"+ output);
//            log.info("==============arg 5");
//            return output;
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//            return ""; // 或者抛出异常，根据需求处理
//        }
//    }

    public String exec(String scriptPath, String... args) {

        try {
            // 构建命令行参数，包括 Python 解释器和脚本路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.redirectErrorStream(true);

            // 设置工作目录
            pb.directory(new File(scriptPath));

            // 将参数添加到命令行参数中
            pb.command().addAll(Arrays.asList(args));

            // 启动进程并等待其完成
            Process process = pb.start();

            // 创建 StringBuilder 来收集输出
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();

            // 创建线程读取标准输出流
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append(System.lineSeparator());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


            // 启动线程
            outputThread.start();
//
//            // 创建线程读取标准错误流
//            Thread errorThread = new Thread(() -> {
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        error.append(line).append(System.lineSeparator());
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            errorThread.start();

            // 等待进程完成
            try {
                int exitCode = process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                // 等待线程完成
                outputThread.join();
//                errorThread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


//            log.info("==============arg 8");
//            // 等待进程终止，并获取退出值
//            int exitCode = process.waitFor();
//            System.out.println("==================Exited with code: " + exitCode);
//
//            log.info("==============arg 1");
//            // 读取进程输出
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
//
//            log.info("==============arg 2");
////            String output = reader.lines().toString();
//            String output = reader.lines().collect(Collectors.joining("\n"));
//
//            log.info("==============arg 3");
//            // 关闭输入流
//            reader.close();


            // 返回执行结果
            String cleanedOutput = output.toString().replaceAll("[\n\r]", "");
            return cleanedOutput;

        } catch (IOException e) {
            e.printStackTrace();
            return ""; // 或者抛出异常，根据需求处理
        }
    }

    public String exec_exe(String scriptPath, String scriptName, String... args) {
        try {
            // 构建命令行参数
            List<String> command = new ArrayList<>();
            command.add(scriptPath + File.separator + scriptName);
            command.addAll(Arrays.asList(args));

            // 初始化 ProcessBuilder
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(scriptPath));
            pb.redirectErrorStream(true); // 将错误流合并到标准输出流

            // 启动进程
            Process process = pb.start();

            StringBuffer output = new StringBuffer();// StringBuilder

            // 创建线程读取标准输出流
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append(System.lineSeparator());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // 启动线程
            outputThread.start();

            // 等待进程完成
            try {
                int exitCode = process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                // 等待线程完成
                outputThread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String cleanedOutput = output.toString().replaceAll("[\n\r]", "");
            return cleanedOutput;

        } catch (IOException e) {
            e.printStackTrace();
            return ""; // 或者根据需求抛出异常
        }
    }

    /**
     *  ExecutorService 来处理进程的等待。创建一个线程池来提交进程的等待任务，并通过 Future 对象来控制超时
     * @param scriptPath
     * @param args
     * @return
     */

//    public String exec_exe(String scriptPath, String scriptName, String... args) throws IOException {
//        StringBuilder output = new StringBuilder();
//        StringBuilder error = new StringBuilder();
//
//        ProcessBuilder pb = new ProcessBuilder(scriptPath + File.separator + scriptName);
//        pb.directory(new File(scriptPath));
//        pb.redirectErrorStream(true);
//
//        Process process = pb.start();
//
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        Future<Integer> future = executor.submit(() -> {
//            try {
//                return process.waitFor();
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt(); // Preserve interrupt status
//                return null;
//            }
//        });
//
//        try {
//            int exitCode = future.get(30, TimeUnit.SECONDS); // 等待最多30秒
//            if (exitCode != 0) {
//                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
//                    String line;
//                    while ((line = errorReader.readLine()) != null) {
//                        error.append(line).append(System.lineSeparator());
//                    }
//                }
//                throw new IOException("Process exited with code " + exitCode + ": " + error.toString());
//            }
//            try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                String line;
//                while ((line = outputReader.readLine()) != null) {
//                    output.append(line).append(System.lineSeparator());
//                }
//            }
//            return output.toString().replaceAll("[\n\r]", "");
//
//        } catch (TimeoutException e) {
//            future.cancel(true); // 取消进程的等待
//            process.destroy(); // 超时后终止进程
//            throw new IOException("Process timed out", e);
//        } catch (InterruptedException | ExecutionException e) {
//            throw new IOException("Error while waiting for process", e);
//        } finally {
//            executor.shutdownNow(); // 关闭线程池
//        }
//    }


//    public String exec_exe(String scriptPath, String scriptName, String... args) {
//        try {
//            // 构建命令行参数
//            ProcessBuilder pb = new ProcessBuilder();
//            pb.redirectErrorStream(true);
//
//
//            // 设置工作目录
//            pb.directory(new File(scriptPath));
//            pb.command().add(scriptPath + File.separator + scriptName); // 将 .exe 文件的路径作为命令
//            // 将参数添加到命令行参数中
//            pb.command().addAll(Arrays.asList(args));
//
//            // 启动进程并等待其完成
//            Process process = pb.start();
//
//            // 创建 StringBuilder 来收集输出
//            StringBuilder output = new StringBuilder();
//            StringBuilder error = new StringBuilder();
//
//            // 创建线程读取标准输出流
//            Thread outputThread = new Thread(() -> {
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        output.append(line).append(System.lineSeparator());
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            // 创建线程读取标准错误流
//            Thread errorThread = new Thread(() -> {
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        error.append(line).append(System.lineSeparator());
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            // 启动线程
//            outputThread.start();
//            errorThread.start();
//
//            // 等待进程完成
//            try {
//                int exitCode = process.waitFor();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//
//            // 等待线程完成
//            try {
//                // 等待线程完成
//                outputThread.join();
//                errorThread.join();
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            // 返回执行结果
//            String cleanedOutput = output.toString().replaceAll("[\n\r]", "");
//            return cleanedOutput;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ""; // 或者抛出异常，根据需求处理
//        }
//    }

    public static String execPy(String scriptPath, String... args) {
        try {
            // 构建命令行参数，包括 Python 解释器和脚本路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.redirectErrorStream(true);

            // 设置工作目录
            pb.directory(new File(scriptPath));

            // 将参数添加到命令行参数中
            pb.command().addAll(Arrays.asList(args));

            // 启动进程并等待其完成
            Process process = pb.start();

            // 创建 StringBuilder 来收集输出
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();

            // 创建线程读取标准输出流
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append(System.lineSeparator());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // 创建线程读取标准错误流
            Thread errorThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        error.append(line).append(System.lineSeparator());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // 启动线程
            outputThread.start();
            errorThread.start();

            // 等待进程完成
            try {
                int exitCode = process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // 等待线程完成
            try {
                // 等待线程完成
                outputThread.join();
                errorThread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


//            log.info("==============arg 8");
//            // 等待进程终止，并获取退出值
//            int exitCode = process.waitFor();
//            System.out.println("==================Exited with code: " + exitCode);
//
//            log.info("==============arg 1");
//            // 读取进程输出
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
//
//            log.info("==============arg 2");
////            String output = reader.lines().toString();
//            String output = reader.lines().collect(Collectors.joining("\n"));
//
//            log.info("==============arg 3");
//            // 关闭输入流
//            reader.close();

            // 返回执行结果
            String cleanedOutput = output.toString().replaceAll("[\n\r]", "");
            return cleanedOutput;
        } catch (IOException e) {
            e.printStackTrace();
            return ""; // 或者抛出异常，根据需求处理
        }
    }

}
