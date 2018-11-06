package com.ysstest.hdfs;

import org.apache.flume.Context;
import org.apache.flume.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: ad
 * Date: 2018-03-21
 * Time: 9:24
 */
public class WriterHdfs implements Configurable {
    private FileSystem fs;
    private FSDataOutputStream hdfsOutStream;

    //初始化
    public WriterHdfs(String hdfsPath) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fileSystem = FileSystem.get(conf);
        Path path = new Path(hdfsPath);
        if (fileSystem.exists(path)) {
            fileSystem.delete(path, false);
        } else {
            fs = FileSystem.get(URI.create(hdfsPath), conf);
            hdfsOutStream = fs.create(path);
        }
    }

    //往Hdfs上写数据
    public void writeFile(String data) throws IOException {
        hdfsOutStream.write(data.getBytes(), 0, data.getBytes().length);
    }

    //关闭hdfsOutStream
    public void outStreamClose() throws IOException {
        hdfsOutStream.flush();
        hdfsOutStream.close();
        if (fs != null && !fs.equals("")) {
            fs.close();
        }

    }

    @Override
    public void configure(Context context) {
    }
}
