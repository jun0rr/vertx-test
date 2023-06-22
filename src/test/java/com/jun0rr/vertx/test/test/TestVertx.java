/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.vertx.test.test;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestVertx {
  
  @Test public void test() throws InterruptedException {
    VertxOptions opt = new VertxOptions();
    Vertx vertx = Vertx.vertx(opt);
    HttpServer server = vertx.createHttpServer();
    CountDownLatch cd = new CountDownLatch(1);
    server.requestHandler(req->{
      System.out.printf("=> %s %s%n", req.method(), req.uri());
      MultiMap par = req.params();
      System.out.printf("   Request Parameters:%n");
      par.forEach((k,v)->System.out.printf("    - %s=%s%n", k, v));
      MultiMap hdr = req.headers();
      System.out.printf("   Headers:%n");
      hdr.forEach((k,v)->System.out.printf("    - %s: %s%n", k, v));
      req.setExpectMultipart(true);
      MultiMap form = req.formAttributes();
      System.out.printf("   Form Attributes:%n");
      form.forEach((k,v)->System.out.printf("    - %s=%s%n", k, v));
      req.bodyHandler(buf->{
        System.out.printf("   Body:%n");
        System.out.printf("    - %s%n", buf.toString());
      });
      req.response()
          .setStatusCode(200)
          .putHeader("Server", "Vertx.core-4.4.3")
          .putHeader("Content-Type", "text/html; charset=UTF-8");
      req.response().end("Hello Vertx");
      if(req.uri().contains("shutdown")) {
        cd.countDown();
      }
    });
    server.listen(20202).andThen(res->{
      if(res.succeeded()) {
        System.out.printf("* Started Vertx HttpServer: localhost:%d%n", res.result().actualPort());
      }
      else {
        System.err.printf("#ERROR: %s%n", res.cause().toString());
        res.cause().printStackTrace();
      }
    });
    cd.await();
    server.close();
  }
  
}
