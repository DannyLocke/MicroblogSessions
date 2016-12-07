package com.ironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String, User> userHashMap = new HashMap<>();
    public static ArrayList<Message> list = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        Spark.init();

        //get() method to identify user and/or add new user to HashMap
        Spark.get (
                "/",

                ((request,response) -> {
                    Session session = request.session();
                    String name = session.attribute("createUser");
                    User user = userHashMap.get(name);

                    HashMap n = new HashMap();

                    //if there's no user, login page
                    if(user == null) {
                        return new ModelAndView(n, "index.html");
                    }
                    //create new user/password/message
                    else {

                        n.put("createUser", user.name);
                        n.put("userPassword", user.password);
                        n.put("createMessage", list);
                        return new ModelAndView(n, "messages.html");
                    }
                }),
                new MustacheTemplateEngine()

        );

        //post user and password
        Spark.post(
                "/index",

                ((request, response) -> {
                    Session session = request.session();
                    String name = request.queryParams("createUser");
                    String password = request.queryParams("userPassword");
//                    if(name == null || password == null){
//                        throw new Exception("Please enter name and password.");
//                    }

                    //create object with name & password
                    User user = userHashMap.get(name);
                    if(user == null){
                        user = new User(name, password);
                        userHashMap.put(name, user);
                        userHashMap.put(password,user);
                    }
                    else if (!user.password.equals(password)){
                        throw new Exception("Wrong password.");
                    }

                    session.attribute("createUser", name);
                    session.attribute("userPassword", password);
                    response.redirect("/");
                    return "";
                })
        );

        //post messages
        Spark.post(
                "/createMessages",

                ((request, response) -> {
                    Session session = request.session();
                    String note = request.queryParams("createMessage");

                    User user = userHashMap.get(note);

                    session.attribute("createMessage", note);
                    session.attribute("createMessage", 0);

                    Message x = new Message(note, 0);
                    list.add(x);

                    response.redirect("/");
                    return "";
                })
        );

        //edit messages
        Spark.post(
                "/editMessages",

                ((request, response) -> {
                    Session session = request.session();
                    String note = request.queryParams("editMessage");

                    int num = Integer.valueOf(request.queryParams("number"));

                    User user = userHashMap.get(note);
//                    if (num <= 0 || num - 1 >= user.list.size()) {
//                        throw new Exception("invalid entry");
//                    }

                    list.get(num - 1);
                    Message m1 = new Message(note, 0);

                    m1.note = note;
                    list.set(num - 1, m1);

                    response.redirect("/");
                    return "";
                }
                ));

        //delete messages
        Spark.post(
                "/deleteMessages",
                ((request, response) -> {
                    Session session = request.session();
                    String note = session.attribute("deleteMessage");

                    int num = Integer.valueOf(request.queryParams("number"));

                    User user = userHashMap.get(note);
//                    if(num <= 0 || num - 1 >= user.list.size()){
//                        throw new   Exception("invalid entry");
//                    }
                    list.get(num-1);
                    Message m1 = new Message(note, 0);

                    m1.note = note;
                    list.set(num - 1, m1);

                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );
    }//end main()
}//end class Main