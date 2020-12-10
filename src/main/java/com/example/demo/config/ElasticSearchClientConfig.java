package com.example.demo.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Objects;

@Configuration
public class ElasticSearchClientConfig {
    Logger logger = LoggerFactory.getLogger(ElasticSearchClientConfig.class);

    private static final int ADDRESS_LENGTH = 2;
    private static final String HTTP_SCHEME = "http";

    /**
     * 使用冒号隔开ip和端口1
     */
    @Value("${elasticsearch.ip}")
    String[] ipAddress;

    @Bean(name = "highLevelClient")
    public RestHighLevelClient highLevelClient() {
        HttpHost[] hosts = Arrays.stream(ipAddress)
                .map(this::makeHttpHost)
                .filter(Objects::nonNull)
                .toArray(HttpHost[]::new);
        logger.debug("hosts:{}", Arrays.toString(hosts));
        RestClientBuilder restClientBuilder = RestClient.builder(hosts);
        return new RestHighLevelClient(restClientBuilder);
    }


    private HttpHost makeHttpHost(String s) {
        assert StringUtils.isNotEmpty(s);
        String[] address = s.split(":");
        if (address.length == ADDRESS_LENGTH) {
            String ip = address[0];
            int port = Integer.parseInt(address[1]);
            return new HttpHost(ip, port, HTTP_SCHEME);
        } else {
            return null;
        }
    }

    /*@Bean(name = "restClient")
    public RestClient getClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {
        // 如果有多个从节点可以持续在内部new多个HttpHost，参数1是ip,参数2是HTTP端口，参数3是通信协议
        //RestClientBuilder clientBuilder = RestClient.builder(new HttpHost(esHost, esPort, protocol));

        // 添加其他配置，返回来的还是RestClientBuilder对象，这些配置都是可选的
        // clientBuilder.setXX()...

        // 设置请求头，每个请求都会带上这个请求头
        //Header[] defaultHeaders = {new BasicHeader("header", "value")};
        //clientBuilder.setDefaultHeaders(defaultHeaders);

        // 设置超时时间，多次尝试同一请求时应该遵守的超时。默认值为30秒，与默认套接字超时相同。若自定义套接字超时，则应相应地调整最大重试超时
        clientBuilder.setMaxRetryTimeoutMillis(60000);

        // 设置监听器，每次节点失败都可以监听到，可以作额外处理
        *//*clientBuilder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(Node node) {
                super.onFailure(node);
                System.out.println(node.getName() + "==节点失败了");
            }
        });*//*

        *//* 配置节点选择器，客户端以循环方式将每个请求发送到每一个配置的节点上，
        发送请求的节点，用于过滤客户端，将请求发送到这些客户端节点，默认向每个配置节点发送，
        这个配置通常是用户在启用嗅探时向专用主节点发送请求（即只有专用的主节点应该被HTTP请求命中）
        *//*
        //clientBuilder.setNodeSelector(NodeSelector.SKIP_DEDICATED_MASTERS);

        // 进行详细的配置,自定义选择器
        *//*clientBuilder.setNodeSelector(new NodeSelector() {
            // 设置分配感知节点选择器，允许选择本地机架中的节点（如果有），否则转到任何机架中的任何其他节点。
            @Override
            public void select(Iterable<Node> nodes) {
                boolean foundOne = false;
                for (Node node: nodes) {
                    String rackId = node.getAttributes().get("rack_id").get(0);
                    if ("rack_one".equals(rackId)) {
                        foundOne = true;
                        break;
                    }
                }
                if (foundOne) {
                    Iterator<Node> nodesIt = nodes.iterator();
                    while (nodesIt.hasNext()) {
                        Node node = nodesIt.next();
                        String rackId = node.getAttributes().get("rack_id").get(0);
                        if ("rack_one".equals(rackId) == false) {
                            nodesIt.remove();
                        }
                    }
                }
            }
        });*//*

        *//* 配置异步请求的线程数量，Apache Http Async Client默认启动一个调度程序线程，以及由连接管理器使用的许多工作线程
        （与本地检测到的处理器数量一样多，取决于Runtime.getRuntime().availableProcessors()返回的数量）。线程数可以修改如下,
        这里是修改为1个线程，即默认情况
        *//*
        *//*clientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                return httpAsyncClientBuilder.setDefaultIOReactorConfig(
                        IOReactorConfig.custom().setIoThreadCount(1).build()
                );
            }
        });*//*

        *//*
        如果ES设置了密码，那这里也提供了一个基本的认证机制，下面设置了ES需要基本身份验证的默认凭据提供程序
        *//*
        *//*final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("user", "password"));
        clientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        });

        //上面采用异步机制实现抢先认证，这个功能也可以禁用，这意味着每个请求都将在没有授权标头的情况下发送，然后查看它是否被接受，
        //并且在收到HTTP 401响应后，它再使用基本认证头重新发送完全相同的请求，这个可能是基于安全、性能的考虑
        clientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                // 禁用抢先认证的方式
                httpClientBuilder.disableAuthCaching();
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        });*//*

        *//*
        配置通信加密，有多种方式：setSSLContext、setSSLSessionStrategy和setConnectionManager(它们的重要性逐渐递增)
        *//*
        *//*String keyStorePass = null;
        Path keyStorePath = null;
        KeyStore truststore = KeyStore.getInstance("jks");
        try (InputStream is = Files.newInputStream(keyStorePath)) {
            truststore.load(is, keyStorePass.toCharArray());
        }
        SSLContextBuilder sslBuilder = SSLContexts.custom().loadTrustMaterial(truststore, null);
        final SSLContext sslContext = sslBuilder.build();
        clientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setSSLContext(sslContext);
            }
        });*//*

        // 最后配置好的clientBuilder再build一下即可得到真正的Client
        return clientBuilder.build();
    }*/

}
