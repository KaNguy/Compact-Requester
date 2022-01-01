package tests;

import core.Request;

public class RequestTests {
    public static void main(String[] args) {
        Request request1 = new Request("https://docs.scala-lang.org/", "GET", null);
        System.out.println(request1.output);

        Request request2 = new Request("http://localhost:8080/echo", "POST", "{\"message\": \"POST message\"}");
        System.out.println(request2.output);

        Request request3 = new Request(
                "https://api.github.com/",
                "GET",
                null,
                new String[][] {
                        {"Accept", "*/*"},
                        {"User-Agent", "*"}
                });
        System.out.println(request3.output);

        Request request4 = new Request("http://localhost:8080/echo", "{\"message\": \"POST message\"}");
        System.out.println(request4.output);
        System.out.println(request4.getResponseHeaders());

        String getRequest1 = Request.get("https://api.github.com/", null);
        System.out.println(getRequest1);

        String getRequest2 = Request.get("https://api.github.com/", new String[][] { {"User-Agent", "*"}, {"Accept", "*/*"} });
        System.out.println(getRequest2);
    }
}
