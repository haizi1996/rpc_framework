package framework_rpc.registry;

public interface Constant {
	
	//zk集群的地址
	String ZK_IPADDRESS = "haizi:2181";
	//会话超时
	int ZK_SESSION_TIMEOUT = 5000;
	//连接超时
	int ZK_CONNECTION_TIMEOUT = 10000;
    //zookeeper的znode根路径
	String ZK_REGISTRY_PATH = "/registry";
    //zookeeper上注册的服务的根路径
	String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}
