package com.ironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.HashMap;

public class Main {

    //to store users
    static HashMap<String, User> userHashMap = new HashMap<>();

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
                        n.put("createMessage", User.list);
                        return new ModelAndView(n, "messages.html");
                    }
                }),
                new MustacheTemplateEngine()
        );//end Spark.get

        //post user and password
        Spark.post(
                "/index",

                ((request, response) -> {
                    Session session = request.session();
                    String name = request.queryParams("createUser");
                    String password = request.queryParams("userPassword");

                    if(name == null || password == null){
                        throw new Exception("Please enter name and password.");
                    }

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
        );//end Spark.post /index

        //post messages
        Spark.post(
                "/createMessages",

                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("createUser");
                    User user = userHashMap.get(name);

                    if(user == null){
                        throw new Exception("Please log in first");
                    }

                    String note = request.queryParams("createMessage");

                    Message x = new Message(note);
                    user.list.add(x);

                    response.redirect("/");
                    return "";
                })
        );//end Spark.post /createMessages

        //edit messages
        Spark.post(
                "/editMessages",

                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("createUser");

                    User user = userHashMap.get(name);

                    String num = request.queryParams("num");
                    int x = Integer.parseInt(num);

                    //specifies which message to select and edit
                    user.list.get(x - 1);
                    user.list.remove(x - 1);

                    //repost edited message
                    String editMessage = request.queryParams("editMessage");
                    Message note = new Message(editMessage);
                    user.list.add(x - 1, note);


                    response.redirect("/");
                    return "";
                }
                ));//end Spark.post /editMessages

        //delete messages
        Spark.post(
                "/deleteMessages",
                ((request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("createUser");

                    User user = userHashMap.get(name);

                    String deleteMessage = request.queryParams("deleteMessage");

                    int x = Integer.parseInt(deleteMessage);
                    user.list.remove(x - 1);

                    response.redirect("/");
                    return "";
                })
        );//end Spark.post /deleteMessages

        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );//end Spark.post /logout

    }//end main()

}//end class Main