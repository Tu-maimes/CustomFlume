package com.ysstest;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
import java.util.Vector;

public class CommonJoinMainDemo   extends Configured implements Tool {


    public class CommonJoinMap extends Mapper<LongWritable, Text, Text, Text> {

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            // 获取输入文件的全路径和名称
            FileSplit fileSplit = (FileSplit) context.getInputSplit();
            String path = fileSplit.getPath().toString();

            // 获取输入记录的字符串
            String line = value.toString();

            // 处理来自emp表的记录
            if (path.contains("emp")){
                // 按空格切割
                String[] values = line.split(",");
                // 获取emp表的部门编号和员工名字
                String deptNo = values[7];
                String empName = values[1];

                // 把结果写出去，打标签
                context.write(new Text(deptNo), new Text("a#" + empName));
            } else if (path.contains("dept")){
                // 按空格符切割
                String[] values = line.split(",");
                // 获取dept表的部门编号、部门名称、城市
                String deptNo = values[0];
                String deptName = values[1];
                String city = values[2];

                // 把结果写出去，打标签
                context.write(new Text(deptNo), new Text("b#" + deptName + " " + city));
            }
        }

    }




    public class CommonJoinReduce extends Reducer<Text, Text, Text, Text> {

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            // 用于存放来自emp表的数据
            Vector<String> vectorA = new Vector<String>();
            // 用于存放来自dept表的数据
            Vector<String> vectorB = new Vector<String>();

            // 迭代集合数据
            for (Text val : values){
                // 将集合中的数据 对应 添加到  相应的  Vector中去
                // 根据标签 ，将 a# 和 b# 之后的数据全部提取出来
                // 相当于去除标签
                if (val.toString().startsWith("a#")){
                    vectorA.add(val.toString().substring(2));
                } else if (val.toString().startsWith("b#")){
                    vectorB.add(val.toString().substring(2));
                }
            }

            // 获取两个Vector集合的长度
            int sizeA = vectorA.size();
            int sizeB = vectorB.size();

            // 遍历两个向量，将结果写出去
            for (int i = 0 ; i < sizeA ; i ++){
                for (int j = 0 ; j < sizeB ; j ++){
                    context.write(key, new Text(" " + vectorA.get(i) + " " + vectorB.get(j)));
                }
            }
        }

    }




    @Override
    public int run(String[] args) throws Exception {



        // 实例化作业对象，设置作业名称、Mapper和Reduce类
        Job job = Job.getInstance(getConf(),"CommonJoinMapDemo") ;
        job.setJarByClass(CommonJoinMainDemo.class);



        job.setMapperClass(CommonJoinMap.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        // 设置输入格式类
        job.setInputFormatClass(TextInputFormat.class);

        // 指定Reducer类和输出key和value的类型
        job.setReducerClass(CommonJoinReduce.class);

        // 设置输出格式
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);


        // 第1个参数为缓存的部门数据路径、第2个参数为员工数据路径和第3个参数为输出路径
        String[] otherArgs = new GenericOptionsParser(job.getConfiguration(), args).getRemainingArgs();
        FileInputFormat.setInputPaths(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        job.waitForCompletion(true);
        return job.isSuccessful() ? 0 : 1;
    }


    /**
     * 主方法，执行入口
     * @param args 输入参数
     */
    public static void main(String[] args) throws Exception {
        // 第1个参数为缓存的部门数据路径、第2个参数为员工数据路径和第3个参数为输出路径

        args = new String[2];
        args[0] = "/tmp/zl/mr/input/" ;
        args[1] = "/tmp/zl/mr/output/CommonJoinMapDemo" ;
        String[] otherArgs = new String[]{"C:\\Users\\lxd\\Desktop\\ff", "C:\\Users\\lxd\\Desktop\\ff\\ddd2.txt"};
        //清理数据
//        HDFSUtils.removeFile(args[1]);

        int res = ToolRunner.run(new Configuration(), new CommonJoinMainDemo(), otherArgs);

//        HDFSUtils.readFile(args[1]+"/part-r-00000");

        System.exit(res);
    }
}
