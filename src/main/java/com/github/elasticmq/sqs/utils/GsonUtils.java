package com.github.elasticmq.sqs.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-09-05
 * Time: 3:40 pm
 */
public class GsonUtils {
	private static final String EMPTY_JSON = "{}";
	private static final String EMPTY_JSON_ARRAY = "[]";
	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static String toJson(Object target) {
		return toJson(target, null, null);
	}

	private static ConcurrentHashMap<String, Gson> gsonCache = new ConcurrentHashMap<String, Gson>();

	public static String toJson(Object target, Type targetType, String datePattern) {
		if (target == null) {
			return EMPTY_JSON;
		}
		if (datePattern == null || datePattern.length() < 1) {
			datePattern = DEFAULT_DATE_PATTERN;
		}
		Gson gson = getGsonWithCache(datePattern);
		String result = EMPTY_JSON;
		try {
			if (targetType == null) {
				result = gson.toJson(target);
			} else {
				result = gson.toJson(target, targetType);
			}
		} catch (Exception ex) {
			if (target instanceof Collection<?> || target instanceof Iterator<?> || target instanceof Enumeration<?>
					|| target.getClass().isArray()) {
				result = EMPTY_JSON_ARRAY;
			}
		}
		return result;
	}

	private static Gson getGsonWithCache(String datePattern) {
		Gson gson;
		if (!gsonCache.containsKey(datePattern)) {
			GsonBuilder builder = new GsonBuilder()
					.setDateFormat(datePattern)
					.registerTypeAdapter(DateTime.class, new DateTimeToLongAdapter())
//					.registerTypeAdapter(DateTime.class, new DateTimeAdapter())
					.registerTypeAdapter(BigDecimal.class, new BigDecimalAdapter());
			gsonCache.putIfAbsent(datePattern, builder.create());
		}
		gson = gsonCache.get(datePattern);
		return gson;
	}

	public static String toJson(Object target, Type targetType) {
		return toJson(target, targetType, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromJson(String json, TypeToken<T> token, String datePattern) {
		return (T) fromJson(json, token.getType(), datePattern);
	}

	public static Object fromJson(String json, Type type, String datePattern) {
		if (json == null || json.length() < 1) {
			return null;
		}

		if (datePattern == null || datePattern.length() < 1) {
			datePattern = DEFAULT_DATE_PATTERN;
		}

		Gson gson = getGsonWithCache(datePattern);
		try {
			return gson.fromJson(json, type);
		} catch (Exception ex) {
			return null;
		}
	}

	public static Object fromJson(String json, Type type) {
		return fromJson(json, type, null);
	}

	public static <T> T fromJson(String json, TypeToken<T> token) {
		return fromJson(json, token, null);
	}

	public static <T> T fromJson(String json, Class<T> clazz, String datePattern) {
		if (json == null || json.length() < 1) {
			return null;
		}
		if (datePattern == null || datePattern.length() < 1) {
			datePattern = DEFAULT_DATE_PATTERN;
		}
		Gson gson = getGsonWithCache(datePattern);
		try {
			return gson.fromJson(json, clazz);
		} catch (Exception ex) {
			// logger.error(json + " 无法转换为 " + clazz.getName() + " 对象!", ex);
			return null;
		}
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		return fromJson(json, clazz, null);
	}

	public static void main(String[] args) {

		Boolean flag = Boolean.TRUE;
		String json = GsonUtils.toJson(flag);
		// System.out.println(json);
		Boolean newflag = GsonUtils.fromJson(json, Boolean.class);
		// System.out.println(newflag);

		Map map = new HashMap();
		map.put("id", Integer.valueOf(1));
		Map map2 = new HashMap();
		map2.put("id", Integer.valueOf(2));
		List<Map> list = new ArrayList<Map>();
		list.add(map);
		list.add(map2);
		String json2 = GsonUtils.toJson(list);
		System.out.println(json2);

		Map zhmap = new HashMap();
		zhmap.put("test", "yangkuan");
		String zh = GsonUtils.toJson(zhmap, new TypeToken<HashMap>() {
		}.getType(), "yyyy-MM-dd HH:mm:ss");
		System.out.println(zh);
		System.out.println(GsonUtils.toJson(zhmap));
	}
}

class BigDecimalAdapter implements JsonSerializer<BigDecimal>, JsonDeserializer<BigDecimal> {

	@Override
	public BigDecimal deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		if (json == null) {
			return null;
		} else {
			try {
				return json.getAsBigDecimal();
			} catch (Exception e) {
				return null;
			}
		}
	}

	final static DecimalFormat decimalFormat = new DecimalFormat("0.0#####################");

	@Override
	public JsonElement serialize(BigDecimal src, Type type, JsonSerializationContext jsonSerializationContext) {
		String value = "";
		if (src != null) {
			value = String.valueOf(decimalFormat.format(src));
		}
		return new JsonPrimitive(value);
	}
}

class DateTimeAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

	@Override
	public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		if (json == null) {
			return null;
		} else {
			try {
				return new DateTime().withMillis(TimeUtils.parseDate(TimeUtils.COMMON_FORMAT, json.getAsString()).getTime());
			} catch (Exception e) {
				return null;
			}
		}
	}

	@Override
	public JsonElement serialize(DateTime src, Type type, JsonSerializationContext jsonSerializationContext) {
		String value = "";
		if (src != null) {
			value = TimeUtils.formatDate(TimeUtils.COMMON_FORMAT, src.toDate());
		}
		return new JsonPrimitive(value);
	}
}

class DateTimeToLongAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

	@Override
	public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		if (json == null) {
			return null;
		} else {
			try {
				return new DateTime().withMillis(json.getAsLong());
			} catch (Exception e) {
				return null;
			}
		}
	}

	@Override
	public JsonElement serialize(DateTime src, Type type, JsonSerializationContext jsonSerializationContext) {
		Long value = null;
		if (src != null) {
			value = src.getMillis();
		}
		return new JsonPrimitive(value);
	}
}