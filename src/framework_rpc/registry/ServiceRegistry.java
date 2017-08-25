package framework_rpc.registry;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.collections4.MapUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ServiceRegistry {

	private CountDownLatch latch = new CountDownLatch(1);
	private String registryAddress;

	public ServiceRegistry(String registryAddress) {
		super();
		this.registryAddress = registryAddress;
	}

	private ZooKeeper connect() {
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					// TODO Auto-generated method stub
					if (event.getState() == Event.KeeperState.SyncConnected) {
						latch.countDown();
					}
				}

			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return zk;
	}

	public void registry(String address, Map<String, Object> beanMap) {
		if (address != null) {
			ZooKeeper zk = connect();
			if (zk != null) {
				addRootNode(zk);
				createNode(zk, address, beanMap);
			}
		}
	}

	private void createNode(ZooKeeper zk, String address, Map<String, Object> beanMap) {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer("");
		if (MapUtils.isNotEmpty(beanMap)) {
			for (String intefaceName : beanMap.keySet()) {
				buffer.append(":").append(intefaceName);
			}
		}
		byte[] bytes = buffer.substring(1).getBytes();
		try {
			String path = zk.create(Constant.ZK_DATA_PATH + "/" + address, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE,
					CreateMode.EPHEMERAL);
			System.out.println("create zookeeper node ({" + path + "} => {" + bytes + "})");
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void addRootNode(ZooKeeper zk) {
		// TODO Auto-generated method stub
		try {
			Stat s = zk.exists(Constant.ZK_REGISTRY_PATH, false);
			if (s == null) {
				zk.create(Constant.ZK_REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				zk.create(Constant.ZK_DATA_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			}

		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
