# rpc_framework
#幷把接口提供给client作为规范，client通过服务接口生成代理类，采用NIO进行底层通讯，先从zookeeper上获取调用服务对应节点的IP和port，之后直接跟具体的服务节点进行通讯，服务节点根据请求的参数，调用相应实现类的方法，将运行结果发送给client端
架构设计：
![/home/haizi/图片/rpc.png](http://)
