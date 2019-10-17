## 接口平台-SDK开发包

> 接口平台是一个对各个不同应用提供数据服务的，对于接口的调用可以使用http调用，但是实际开发中，如果是java语言，为了方便客户端快速开发，我们提供了SDK开发工具包，调用者可以引入SDK开发包，直接通过接口名.方法名的方式进行调用。



### 1.1 概述

`open-api-sdk`是一个利用SDK开发包，实现接口调用的案例项目。项目组成如下：

- open-api-sdk
  - utils 基础工具包，包含接口加密验签、json转换等工具
  - api-sdk-core 开发sdk核心依赖包，依赖utils.jar
  - api-client 接口客户端调用方案例，依赖api-sdk-core，开发sdk
  - api-server 接口服务端提供方案例，依赖utils.jar

以上最核心的包就是`api-sdk-core` 它需要依赖`utils` 因为utils包中含有一些数据校验、接口加密验签、数据格式转换等基础工具。当然这个utils包和接口服务方引用的utils是保持一致的，这样才能保证双方签名或加密解密校验算法保持一致。

### 1.2 如何准备和使用

#### 1.2.1 准备：定义好请求和响应消息体（该案例已提供）

请求类型：统一设置为`POST`请求格式

请求体：

```json
{
    "head":{
        "appId":"应用ID，一个调用者的唯一标识",
        "sessionId":"请求会话ID",
        "requestId":"请求ID",
        "timstamp":1571108440099,//一个时间戳
        "sign":"根据签名秘钥生成的一串签名"
    },
    "reqData":{
          请求数据
    }
}
```

响应体：

```json
{
    "head":{
        "requestId":"4d7ce6c315fd4154a52ab60db9c8791e",
        "retCode":"0",//retCode为0表示响应成功
        "retMsg":"",//返回消息，当retCode为0是此处可为空
        "timstamp":"当前时间戳毫秒数",
        "costTime":"接口处理毫秒数"
    },
    "resData":{
          响应数据
    }
}
```

#### 1.2.2 准备：有一个提供接口的服务（以`api-server`为例）

该案例中以`api-server`为例，它里面提供了 `user`的服务接口，对外提供：用户添加`/user/add`，用户列表`/user/list`，查询某用户`/user/one`三个接口。

为了接口安全，该服务提供了一个接口权限校验的拦截器`ApiValidateInterceptor`，如果是springcloud服务可以将该逻辑写到网关。它里面的逻辑主要包含如下：



![接口权限校验流程图](https://img-blog.csdnimg.cn/20191017151512988.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3hpd2VpbGxlcg==,size_16,color_FFFFFF,t_70)

调用端携带请求参数，接口服务端接收到参数后，进行以下校验步骤：

1. 有效性校验。根据参数timestamp 和系统当前时间对比，时间差是否在指定范围内（10min），如果超出，则返回 请求失效。

2. 合法性校验。根据中台调用端信息表中的appId,signKey和请求中的其他参数，按照和中台调用端一致的签名生成规则，计算得出sign，与调用端请求参数中的sign值进行对比是否相等。如果相等表示验签通过，如果不相等，则验签失败，直接返回 签名不合法

3. 判断是否授权。根据appId查询是否存在该调用者信息。不存在，直接返回 用户未授权

4. 接口权限校验。根据app，service，method等URL参数，唯一确定接口信息，再用接口ID关联角色及权限表查询，判断是否存在于允许访问的接口列表中。如果存在，则允许访问，如果不存在，则返回 接口未授权。

5. 接口调用返回，封装为响应体，并对响应体以中台调用端信息表中的desKey值为秘钥，进行加密后返回给调用者。

**`api-server`提供了一个默认的客户端应用访问账号：**

- 应用ID（appId） : 100001
- 签名秘钥（signSecret）：weiller

#### 1.2.3 使用：创建一个客户端应用（以`api-client`为例），开发一个访问接口（以UserApi为例）

如案例中所示，我们提供一个访问user接口的SDK调用：

```java
@OpenApiService(protocol= OpenApiProtocol.HTTP, name = "用户访问接口")
public interface UserApi extends BaseOpenApi {

    @OpenApi(path = "/user/add", name = "添加用户")
    ApiResponse<UserDto> add(@ApiParam("userDto") UserDto userDto);

    @OpenApi(path = "/user/list", name = "用户列表")
    ApiResponse<List<UserDto>> list();

    @OpenApi(path = "/user/one", name = "是否登录状态")
    ApiResponse<UserDto> one(@ApiParam("id") String id);
}
```

- 创建一个user接口，继承BaseOpenApi。类上注解`@OpenApiService`有两个参数，protocol设置请求类型，此处为HTTP，name表示该接口名称
- 定义方法。方法上注解`@OpenApi`有两个参数，path设置接口请求地址，name表示该方法名称。`@ApiParam`用于描述请求参数，如果参数为基础类型，则请求体reqData为key为注解value，value为参数值的map请求参数。如果是自定义类型，则请求体reqData为自定义实体参数。**一般建议作为自定义类型或map类型，作为请求参数**。
- 所有返回类型均为`ApiResponse` 响应体。泛型根据响应类型可以自定义。

#### 1.2.4 使用：SDK调用

```java
// 创建一个接口访问工厂，参数分别为接口地址，appId，签名秘钥
OpenApiFactory openApiFactory = OpenApiFactoryHelper.getOpenApiFactory("http://localhost:8080/api", "100001", "weiller");
// 创建接口
UserApi userApi = openApiFactory.create(UserApi.class);
// 调用方法
UserDto userDto = new UserDto();
userDto.setId("105");
userDto.setName("测试用户");
userDto.setAge(10);
ApiResponse<UserDto> add = userApi.add(userDto);
// 响应数据
UserDto resData = add.getResData();
```

### 1.3 支持其他方法，直接rest方式调用

同样需要引入`api-sdk-core`

```java
// 创建一个rest客户端实例，参数分别是应用Id,sessionId(可为null),签名秘钥
FsRestClient client = new FsRestClient (appId, sessionId, signKey); 
// 发送请求，url是服务端接口URL，json为请求体中的reqData，格式为json对象
client.post(url, json);
// 返回响应体json字符串
String responseJson = client.getResponseContent();

```

