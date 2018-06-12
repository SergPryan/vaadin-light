package org.vaadin.lightvaadin.repository;

import com.mongodb.*;

import java.util.logging.Level;
import java.util.logging.Logger;


public class RepositoryMongoDB {

    private static Logger log = Logger.getLogger(RepositoryMongoDB.class.getName());

    private static RepositoryMongoDB repository;

    public static RepositoryMongoDB getInstance(){
        if(repository ==null){
            return new RepositoryMongoDB();
        } else {
            return repository;
        }
    }

    private RepositoryMongoDB() {
    }

    public synchronized void increaseCounter() {
        MongoClient client = null;
        try {
            client = new MongoClient("localhost",27017);
            DB db  = client.getDB("testdb");
            log.log(Level.INFO,"method update counter");
            DBCollection table = db.getCollection("seq");
            DBObject query = new BasicDBObject();
            query.put("_id", "users_counters");
            DBObject change = new BasicDBObject("seq", 1);
            DBObject update = new BasicDBObject("$inc", change);
            table.findAndModify(query,update);
        } catch (Exception e) {
            log.log(Level.SEVERE,"Exception: " , e);
        }

    }

    public synchronized String getCounter() {
        log.log(Level.INFO,"method get counter");
        MongoClient client = null;
        try {
            client = new MongoClient("localhost",27017);
            DB db  = client.getDB("testdb");
            DBCollection table = db.getCollection("seq");
            DBObject query = new BasicDBObject();
            query.put("_id", "users_counters");
            DBCursor cursor = table.find(query);
            String result =  cursor.next().get("seq").toString();
            return result;
        } catch (Exception e) {
            log.log(Level.SEVERE,"Exception ",e);
            return "not connect";
        }

    }

//    private void initDbCounters(){
//          log.log(Level.INFO,"method init counter");
//          MongoClient client = new MongoClient("localhost",27017);
//          DB db  = client.getDB("testdb");
//        DBCollection table = db.getCollection("seq")
//        DBObject dbObject = new BasicDBObject();
//        dbObject.put("_id","users_counters");
//        dbObject.put("seq",1);
//        table.insert(dbObject);
//    }

}