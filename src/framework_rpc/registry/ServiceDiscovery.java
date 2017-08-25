package framework_rpc.registry;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ServiceDiscovery {

	private String zkAddress;
	private ZooKeeper zk;
	private CountDownLatch latch = new CountDownLatch(1);

	public ServiceDiscovery(String zkAddress) {
		this.zkAddress = zkAddress;
		zk = connect();
		if (zk != null)
			watchNode(zk);
	}

	private void watchNode(final ZooKeeper zk) {
		// TODO Auto-generated method stub
		try {
			zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher(){

				@Override
				public void process(WatchedEvent event) {
					// TODO Auto-generated method stub
					
				}
				
			});
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};

	}

	public void discovery() {
		zk = connect();

	}

	private ZooKeeper connect() {
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper(zkAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {

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

	public void stop() {
		if (zk != null) {
			try {
				zk.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
