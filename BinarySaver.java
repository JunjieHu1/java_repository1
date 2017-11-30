import java.io.*;
import java.net.*;

import javax.imageio.IIOException;

public class BinarySaver {

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            try{
                //根据命令行参数创建URL对象
                URL url = new URL(args[i]);
                saveBinaryFile(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void saveBinaryFile(URL url) throws IOException {

        //使用URL对象获取URLConnection对象
        URLConnection uc = url.openConnection();
        //获取内容类型，用于检测是否为二进制文件
        String contentType = uc.getContentType();
        //获取内容长度，用于创建暂时保存二进制文件的数组
        int contentLength = uc.getContentLength();
        //开始检测, 如果不是二进制文件则会抛出一个异常
        if(contentType.startsWith("text/") || contentLength == -1) {
            throw new IOException("this is not a binary file!!!");
        } 

        //打开输入流，并自动关闭
        try(InputStream in = uc.getInputStream()) {
            //缓冲输入流
            InputStream bin = new BufferedInputStream(in);
            //开始准备将缓冲区中的输入流暂时存入一个固定大小的数组中
            //设置数组的大小
            byte[] data = new byte[contentLength];
            //设置一个数组索引，为字节码存入数组提供辅助
            int offset = 0;
            //将缓冲区中的字节码一次一个存入数组中
            while (offset < contentLength) {
                //byteReadNum 表示的是一次读取缓冲区中的字节数
                int byteReadNum = bin.read(data, offset, data.length - offset);
                System.out.println(byteReadNum);
                offset += byteReadNum;
            }

            //缓冲区读取完毕后，判断字节是否全部被保存(检测是否有字节丢失)
            //如果字节有丢失，则会抛出一个错误
            if (offset != contentLength) {
                throw new IOException("Only read " + offset + "bytes!!!;Expected " + contentLength + "bytes");
            }

            //准备将数组中的二进制数据保存在一个文件中
            //首先要获得文件名，方便以后创建问件时使用
            //一定要使用URL对象才能获得包含文件名的路径
            String fileName = url.getFile();
            fileName = fileName.substring(fileName.lastIndexOf("/" + 1));
            //创建一个保存二进制的新文件
            try(FileOutputStream out = new FileOutputStream(fileName)) {
                //将数组中的二进制数写入文件中
                out.write(data);
                //对输出流进行冲洗，确保二进制数据写入文件
                out.flush();
            }
        }
    }
}