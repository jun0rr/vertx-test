/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.vertx.test.test;

import com.jun0rr.uncheck.Uncheck;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestVertx {
  
  @Test public void test() throws InterruptedException {
    DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
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
      req.bodyHandler(buf->{
        System.out.printf("[%s] BodyHandler - %s(%d)%n", fmt.format(LocalDateTime.now()), Thread.currentThread().getName(), Thread.currentThread().getId());
        MultiMap form = req.formAttributes();
        System.out.printf("   Form Attributes:%n");
        form.forEach((k,v)->System.out.printf("    - %s=%s%n", k, v));
        System.out.printf("   Body:%n");
        System.out.printf("    - %s%n", buf.toString());
      });
      vertx.executeBlocking(p->{
        LocalDateTime now = LocalDateTime.now();
        Uncheck.call(()->Thread.sleep(5000));
        req.response()
            .setStatusCode(200)
            .putHeader("Server", "Vertx.core-4.4.3")
            .putHeader("Content-Type", "text/html; charset=UTF-8");
        req.response().end(String.format("[%s] Hello Vertx - %s(%d)", fmt.format(now), Thread.currentThread().getName(), Thread.currentThread().getId()));
      }, false);
      if(req.uri().contains("shutdown")) {
        cd.countDown();
      }
    });
    server.listen(20202)
        .onSuccess(s->System.out.printf("* Started Vertx HttpServer => localhost:%d%n", s.actualPort()))
        .onFailure(s->System.err.printf("#ERROR: %s%n", s.toString()));
    cd.await();
    server.close();
  }
  
}
