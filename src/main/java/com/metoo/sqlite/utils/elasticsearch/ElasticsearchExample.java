//package com.metoo.sqlite.utils.elasticsearch;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch.core.SearchResponse;
//import co.elastic.clients.elasticsearch.core.search.Hit;
//import co.elastic.clients.transport.rest_client.RestClientTransport;
//import co.elastic.clients.json.jackson.JacksonJsonpMapper;
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//
//public class ElasticsearchExample {
//
//    public static void main(String[] args) throws Exception {
//        // 创建 RestClient 连接到 Elasticsearch
//        RestClient restClient = RestClient.builder(
//                new HttpHost("192.168.5.205", 9200)).build();
//
//        // 使用 Jackson 作为 JSON 解析库
//        RestClientTransport transport = new RestClientTransport(
//                restClient, new JacksonJsonpMapper());
//
//        // 创建 ElasticsearchClient
//        ElasticsearchClient client = new ElasticsearchClient(transport);
//
//        // 执行简单的搜索查询
//        SearchResponse<MyDocument> search = client.search(s -> s
//                        .index("nat-2024.09.16") // 替换为你的索引名称
//                        .query(q -> q
//                                .matchAll(m -> m) // 查询条件 (这里是 Match All)
//                        ),
//                MyDocument.class // 替换为你实际的文档类
//        );
////        SearchResponse<MyDocument> search = client.search(s -> s
////                        .index("nat-2024.09.16") // 替换为你的索引
////                        .query(q -> q
////                                .match(t -> t
////                                        .field("title")
////                                        .query("example")
////                                )
////                        ),
////                MyDocument.class // 替换为你实际的文档类
////        );
//
//        // 输出搜索结果
//        for (Hit<MyDocument> hit : search.hits().hits()) {
//            System.out.println(hit.source());
//        }
//
//        // 关闭客户端
//        restClient.close();
//    }
//}
//
//// 文档类，用于映射查询结果
//class MyDocument {
//    private String title;
//
//    // Getters 和 Setters
//    public String getTitle() { return title; }
//
//    public void setTitle(String title) { this.title = title; }
//}
//
