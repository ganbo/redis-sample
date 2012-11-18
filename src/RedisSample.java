import java.util.*;

import redis.clients.jedis.*;

public class RedisSample {

	public Jedis jedis = null;
	public JedisPool pool = null;
	public String Key_ID = "global:id";
	public String Key_Set = "global:set";
	public static int stringCount = 100000;
	public static int repeatCount = 1;

	public static void main(String[] args) {
		RedisSample s = new RedisSample();
		s.init();
		s.flushDB();
		long t1 = System.currentTimeMillis();
//		s.addToSet(s.generateTestList());
		long t2 = System.currentTimeMillis();
		System.out.println("It took " + ((double) (t2 - t1)) / 1000
				+ " s time to insert " + stringCount + " different strings by "
				+ repeatCount + " times");
		s.printRedisInfo();
		s.printDBSize();
		s.destory();
	}

	public void init() {
		pool = new JedisPool("localhost", 6379);
		jedis = pool.getResource();
	}

	public void flushDB() {
		jedis.flushDB();
	}

	public void printDBSize() {
		System.out.println("Size of db(0) : " + jedis.dbSize());
	}

	public void printRedisInfo() {
		System.out.println("Info of RedisDB : " + jedis.info());
	}

	public void destory() {
		pool.returnResource(jedis);
		pool.destroy();
	}

	public void setID(String id) {
		jedis.set(Key_ID, id);
	}

	public String incrID() {
		return String.valueOf(jedis.incr("Key_ID"));
	}

	public void addToSet(ArrayList<String> list) {
		String id = "";
		for (int i = 0; i < list.size(); i++) {
			if (jedis.sismember(Key_Set, list.get(i))) {

			} else {
				id = incrID();
				jedis.set(concatenateRedisString("String:", list.get(i)), id);
				jedis.set(id, list.get(i));
				jedis.sadd(Key_Set, list.get(i));
			}
		}
	}

	public ArrayList<String> generateTestList() {
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> l = new ArrayList<String>();
		for (int i = 0; i < stringCount; i++) {
			l.add("httpwwwgooglecom" + String.valueOf(i));
		}
		for (int i = 0; i < repeatCount; i++) {
			list.addAll(l);
		}
		return list;
	}

	public static String concatenateRedisString(String... str) {
		String temp = "";
		for (String s : str) {
			temp += s + ":";
		}
		return temp.substring(0, temp.length() - 1);
	}
}
