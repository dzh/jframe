/*
 * test - database
 * 
 * 
 */ 
use test;
db.createCollection("collection");
db.getCollection("collection").insert("{ item: "card", qty: 15}");
