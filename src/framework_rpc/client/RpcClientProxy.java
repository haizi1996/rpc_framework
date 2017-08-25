package framework_rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import framework_rpc.common.RpcRequest;



public class RpcClientProxy {
	private String host;
	private int port;
	
	public RpcClientProxy(String host, int port) {
		this.host = host;
		this.port = port;
	}


	@SuppressWarnings("unchecked")
	public <T> T createProxy(Class<T> cls){
		return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				// TODO Auto-generated method stub
				IPHandler ipHandler = new IPHandler(cls.getName(), host, port); 
				String ipAddress = ipHandler.getIpAddress();
				if(!org.apache.commons.lang3.StringUtils.isNotEmpty(ipAddress) || ipAddress.contains("####")){
					throw new RuntimeException("Service地址有错!");
				}
				RpcRequest request = new RpcRequest();
				request.setRequestId(UUID.randomUUID().toString());
				request.setClassName(cls.getName());
				request.setMethodName(method.getName());
				request.setParams(args);
				request.setParamTypes(method.getParameterTypes());
				RpcClient rpcClient = new RpcClient(request,ipAddress);
				
				return rpcClient.sendRequest(request);
			}
		});
	}
}
