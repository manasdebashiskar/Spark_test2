1、I install Ubuntu 64bit 15.04 in VM for two computer.


2、create hadoop user and change name(/etc/hostname) and hosts(/etc/hosts) for two computer.
sudo useradd -m hadoop -s /bin/bash
sudo passwd hadoop
sudo adduser hadoop sudo

(/etc/hostname in Help1)
Worker’name is：Help1

(/etc/hostname in Master)
Master’name is：Master

(/etc/hosts both in Help1 and Master)
Master's ip   Master
Help1's ip    Help1


3、update apt and install vim for two computer
sudo apt-get update
sudo apt-get install vim


4、install SSH server , JAVA7 and add ssh password for Master 
sudo apt-get install openssh-server(Help1 also run)
ssh localhost
exit
cd ~/.ssh/ 
ssh-keygen -t rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
scp ~/.ssh/id_rsa.pub hadoop@Help1:/home/hadoop/
cat ~/id_rsa.pub >> ~/.ssh/authorized_keys(just Help1 run)
(____Now, Master can ssh Help1 without password______)
sudo apt-get install openjdk-7-jre openjdk-7-jdk(Help1 also run)


5、Set environment variables in ~/.bashrc for two computer
Details can be found in two files.(two computer is the same)
______________________________set OK__________________________


6、Spark 
Go http://spark.apache.org/downloads.html
downloads spark-1.4.0-bin-hadoop2.4.tgz(unzip /usr/local,rename to spark)
I alter some files（/conf）:
_______________________________________
1、slaves
I add:
Help1
_______________________________________
2、spark-env.sh
I add:
export SCALA_HOME=/usr/local/scala
export SPARK_WORKER_MEMORY=1g
export SPARK_MASTER_IP=Master
export MASTER=spark://Master:7077
export HADOOP_CONF_DIR=/usr/local/hadoop

last,I give the authority
sudo chown -R hadoop:hadoop /usr/local/spark
_______________________________________
7、Hadoop
Go https://archive.apache.org/dist/hadoop/common/hadoop-2.4.1/
downloads hadoop-2.4.1.tar.gz(unzip /usr/local,rename to hadoop)
I alter some files（/etc/hadoop）:
1、core-site.xml
I add:
    <property>
        <name>hadoop.tmp.dir</name>
        <value>file:/home/hadoop/tmp</value>
        <description>Abase for other temporary directories.</description>
    </property>

    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://Master:9000</value>
    </property>

    <property>
        <name>io.file.buffer.size</name>
        <value>131072</value>
    </property>

    <property>
        <name>hadoop.proxyuser.hadoop.hosts</name>
        <value>*</value>
    </property>

    <property>
        <name>hadoop.proxyuser.hadoop.groups</name>
        <value>*</value>
    </property>
_______________________________________
2、hdfs-site.xml
I add:
    <property>
        <name>dfs.namenode.secondary.http-address</name>
        <value>Master:9001</value>
    </property>

    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:/home/hadoop/hdfs/namenode</value>
    </property>

    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:/home/hadoop/hdfs/datanode</value>
    </property>

    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>

    <property>
        <name>dfs.webhdfs.enabled</name>
        <value>true</value>
    </property>
_______________________________________
3、mapred-site.xml
I add:
 <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>

    <property>
        <name>mapreduce.jobhistory.address</name>
        <value>Master:10020</value>
    </property>

    <property>
        <name>mapreduce.jobhistory.webapp.address</name>
        <value>Master:19888</value>
    </property>
_______________________________________
4、yarn-env.xml
I add:
<property>
        <name>yarn.resourcemanager.hostname</name>
        <value>Master</value>
    </property>

    <property>
        <name>yarn.resourcemanager.address</name>
        <value>Master:8032</value>
    </property>

    <property>
        <name>yarn.resourcemanager.scheduler.address</name>
        <value>Master:8030</value>
    </property>

    <property>
        <name>yarn.resourcemanager.resource-tracker.address</name>
        <value>Master:8031</value>
    </property>

    <property>
        <name>yarn.resourcemanager.admin.address</name>
        <value>Master:8033</value>
    </property>

    <property>
        <name>yarn.resourcemanager.webapp.address</name>
        <value>Master:8088</value>
    </property>

    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>

    <property>
        <name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
        <value>org.apache.hadoop.mapred.ShuffleHandler</value>
    </property>
_______________________________________
5、master
I add:
Master
_______________________________________
6、slaves
I add:
Help1
_______________________________________
7、hadoop-env.sh
I change
export HADOOP_OPTS="$HADOOP_OPTS -Djava.net.preferIPv4Stack=true"
to:
export HADOOP_COMMON_LIB_NATIVE_DIR=${HADOOP_HOME}/lib/native
export HADOOP_OPTS="-Djava.library.path=${HADOOP_HOME}/lib/native/"
(Because the hadoop from the web is compile to 32bit ,I compile it to 64bit for my 64bit system,and replace the files in usr/local/hadoop/lib/native )

last,I give the authority
sudo chown -R hadoop:hadoop /usr/local/hadoop
_______________________________________


8、Scala
Go  http://www.scala-lang.org/download/2.10.4.html
downloads scala-2.10.4.tgz(unzip /usr/local,rename to scala)

last,I give the authority
sudo chown -R hadoop:hadoop /usr/local/scala


9、scp to Worker and unzip
(in Master)
cd /usr/local
sudo tar -zcf ./spark.tar.gz ./spark
sudo tar -zcf ./hadoop.tar.gz ./hadoop
sudo tar -zcf ./scala.tar.gz ./scala
scp ./spark.tar.gz hadoop@Help1:/home/hadoop
scp ./hadoop.tar.gz hadoop@Help1:/home/hadoop
scp ./scala.tar.gz hadoop@Help1:/home/hadoop

(in Help1)(~/.bashrc is the same as Master)
sudo tar -zxf ~/spark.tar.gz -C /usr/local
sudo tar -zxf ~/hadoop.tar.gz -C /usr/local
sudo tar -zxf ~/scala.tar.gz -C /usr/local
sudo chown -R hadoop:hadoop /usr/local/spark
sudo chown -R hadoop:hadoop /usr/local/hadoop
sudo chown -R hadoop:hadoop /usr/local/scala

10、write the start-mybig.sh and stop-mybig.sh to start or stop
Details can be found in files.

11、(spark-assembly-1.4.0-hadoop2.4.0.jar in my code is from /usr/local/spark/lib)

run test.scala
..can't find class.

run some example like SparkPi,Word....... 
success.

run test1.scala
success.
