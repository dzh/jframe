/**
 * 
 */
package jframe.mongodb.client.service;

import jframe.core.plugin.annotation.Service;

import com.mongodb.MongoClient;

/**
 * @author dzh
 * @date Jul 6, 2015 3:44:16 PM
 * @since 1.0
 */
@Service(clazz = "jframe.mongodb.client.service.impl.MongoClientServiceImpl", id = "jframe.service.mongoclient")
public interface MongoClientService {

	MongoClient getClient(String id);

}
