package com.joeyvmason.serverless.spring.application;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextUtilities {
	public static boolean isPrimitive2(Class<?> c) {
		return (c.isPrimitive() || c.isEnum() || c==Object.class || c==String.class || c==short.class ||  c==Long.class || c== Integer.class || c==Float.class || c==Double.class  || c==Boolean.class || c==Byte.class || c==Character.class || c==java.util.Date.class || c==java.sql.Timestamp.class);
	}
	public static String dump(Object object) {
		
		if(object instanceof String) {
			return (String)object;
		}
		
		return new DumpContext(32,0).dump(object);
	}
	static protected char[] TABS = new char[99];
	static {
		for (int ii=0;ii<TABS.length;ii++) {
			TABS[ii] ='\t';
		}
	}
	static class DumpContext
	{
		int							maxDepth		= 32;
		int							maxList			= 0;
		int							currentDepth	= 0;
		Set<Object>					visitSet		= new HashSet<>();
		StringBuffer				buffer			= new StringBuffer();
		
		DumpContext()
		{
		}
		DumpContext(int maxDepth, int maxList)
		{
			this.maxDepth = maxDepth;
			this.maxList = maxList;
		}
		
		DumpContext tabs()
		{
			int t = currentDepth;
			if (t<0) t = 0;
			if (t>=TABS.length) t=TABS.length-1;
			buffer.append(TABS, 0, t);
			return this;
		}
		
		DumpContext tabs(int diff)
		{
			int t = currentDepth+diff;
			if (t<0) t = 0;
			if (t>=TABS.length) t=TABS.length-1;
			buffer.append(TABS, 0, t);
			return this;
		}
		
		DumpContext nl()
		{
			buffer.append(CommonConstants.EOL);
			return this;
		}
		
		DumpContext lb()
		{
			buffer.append("{");
			return this;
		}
		
		DumpContext rb()
		{
			buffer.append("}");
			return this;
		}

		DumpContext idx(int i)
		{
			buffer.append("[").append(i).append("] ");
			return this;
		}
		
		DumpContext eq(String name)
		{
			buffer.append(name).append(" = ");
			return this;
		}
		
		DumpContext eq(int idx)
		{
			buffer.append("[").append(idx).append("] = ");
			return this;
		}
		
		DumpContext nil()
		{
			buffer.append("{null}");
			return this;
		}
		DumpContext ignore()
		{
			buffer.append("{ignore}");
			return this;
		}
		DumpContext comma()
		{
			buffer.append(",");
			return this;
		}
		
		DumpContext append(String s)
		{
			buffer.append(s);
			return this;
		}
		
		DumpContext clazz(Object object)
		{
			if (object != null) {
				buffer.append(object.getClass().getSimpleName()).append(" ");
			}
			return this;
		}

		boolean simple(Object o)
		{
			return simple(null, o);
		}
		
		boolean simple(Object k, Object o)
		{
			if (o==null) {
				if (k != null) {
					buffer.append(k).append("=");
				}
				buffer.append("{null}");
				return true;
			}
			Class<?> c = o.getClass();
			if (isPrimitive2(c)) {
				if (k != null) {
					buffer.append(k).append("=");
				}
				buffer.append(o);
				return true;
			}
			return false;
		}
		
		DumpContext _dump(Object object)
		{
			if (simple(object)) {
				return this;
			}
			if (maxDepth > 0 && currentDepth > maxDepth) {
				append("{ ... }");
				return this;
			}
			try {
				currentDepth++;

				Class<?> clazz = object.getClass();

				if (visitSet.contains(object)) {
					buffer.append("{@").append(object.hashCode()).append(" ").append(clazz.getSimpleName()).append("}");
					return this;
				}
				visitSet.add(object);
				

				clazz(object);

				if (object instanceof List || object instanceof Set) {
					Collection<?> collection = (Collection<?>)object;
					int idx = 0;
					boolean sim = true;
					lb();
					for (Object element : collection) {
						if (idx>0) {
							comma();
						}
						if (maxList > 0 && idx > maxList) {
							append("... size=").append(""+collection.size());
							break;
						}
						if (!(sim = simple(element))) {
							nl().tabs().eq(idx);
							_dump(element);
						}
						++idx;
					}
					if (!sim) nl().tabs(-1);
					rb();
				}
				else if (object instanceof Map) {
					lb();
					int idx = 0;
					boolean sim = true;
					Map<?,?> map = (Map<?,?>)object;
					for (Map.Entry<?, ?> ent : map.entrySet()) {
						if (idx>0) {
							comma();
						}
						if (maxList > 0 && idx > maxList) {
							append("... size=").append(""+map.size());
							break;
						}
						Object key = ent.getKey();
						Object value = ent.getValue();
						if (!(sim=simple(key, value))) {
							nl().tabs().eq(idx);
							append(String.valueOf(key));
							append("=");
							_dump(value);
						}
						++idx;						
					}
					if (!sim) nl().tabs(-1);
					rb();
				}
				else if (clazz.isArray()) {
					int cnt = Array.getLength(object);
					boolean sim = true;
					lb();
					for (int idx = 0; idx < cnt; idx++) {
						if (idx>0) {
							comma();
						}
						if (maxList > 0 && idx > maxList) {
							append("... length=").append(""+cnt);
							break;
						}
						Object element = Array.get(object, idx);
						if (!(sim=simple(element))) {
							nl().tabs().eq(idx);
							_dump(element);
						}
					}
					if (!sim) nl().tabs(-1);
					rb();
				}
				else {
					lb();
					Class<?> superClass = clazz.getSuperclass();
					String className = superClass.getName();
					if (superClass!=null && (className.startsWith("com.abc")||className.startsWith("abc."))) {
						Field[] fields = superClass.getDeclaredFields();
						for (Field field : fields) {
							int modifier = field.getModifiers();
							if (Modifier.isStatic(modifier) || Modifier.isTransient(modifier)) {
								continue;
							}
							nl().tabs().eq(field.getName());
							try {
								field.setAccessible(true);
								Object value = field.get(object);
								_dump(value);
							}
							catch (Exception ex) {
								//intentional
							}
						}
					}
					Field[] fields = clazz.getDeclaredFields();
					for (Field field : fields) {
						int modifier = field.getModifiers();
						if (Modifier.isStatic(modifier) || Modifier.isTransient(modifier)) {
							continue;
						}
						nl().tabs().eq(field.getName());
						try {
							field.setAccessible(true);
							Object value = field.get(object);
							_dump(value);
						}
						catch (Exception ex) {
							//intentional
						}
					}
					nl().tabs(-1).rb();
				}
			}
			finally {
				currentDepth--;
			}
			
			return this;
		}

		public String dump(Object object)
		{
			try {
				_dump(object);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			return buffer.toString();
		}
	}
}
