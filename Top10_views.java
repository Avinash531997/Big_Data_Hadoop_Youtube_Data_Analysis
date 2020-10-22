import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

   public class Top10_views
   {

   public static class Map extends Mapper<LongWritable, Text, Text,IntWritable>
   {

       private Text videoID = new Text();
       private  IntWritable rating = new IntWritable();
       public void map(LongWritable key, Text value, Context context )throws IOException, InterruptedException
	   {
           String line = value.toString();
           String str[]=line.split("\t");
           if(str.length > 7)
          {
                videoID.set(str[0]);
                //float f=Float.parseFloat(str[5]);
				int f = Integer.parseInt(str[5]); 
                rating.set(f);
                
          }
      context.write(videoID, rating);
      }

    }

    public static class Reduce extends Reducer<Text, IntWritable,Text, IntWritable>
	{

       public void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException
	   {
           int sum = 0;
           int l=0;
           for (IntWritable val : values)
		   {
               l+=1;
               sum += val.get();
           }
           sum=sum/l;
           context.write(key, new IntWritable(sum));
       }
    }

    public static void main(String[] args) throws Exception
	{
       Configuration conf = new Configuration();
           @SuppressWarnings("deprecation")
           Job job = new Job(conf, "Top10_views");
           job.setJarByClass(Top10_views.class);
           job.setMapOutputKeyClass(Text.class);
           job.setMapOutputValueClass(IntWritable.class);
      //job.setNumReduceTasks(0);
       job.setOutputKeyClass(Text.class);
       job.setOutputValueClass(IntWritable.class);

       job.setMapperClass(Map.class);
       job.setReducerClass(Reduce.class);

       job.setInputFormatClass(TextInputFormat.class);
       job.setOutputFormatClass(TextOutputFormat.class);

       FileInputFormat.addInputPath(job, new Path(args[0]));
       FileOutputFormat.setOutputPath(job, new Path(args[1]));
        Path out=new Path(args[1]);
        out.getFileSystem(conf).delete(out);
       job.waitForCompletion(true);
    }

  }
