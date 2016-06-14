package test.test_module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MyClassLoader extends ClassLoader
{

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		byte[] data = getByteArray(name);
		if (data == null)
		{
			throw new ClassNotFoundException();
		}
		return defineClass(name, data, 0, data.length);
	}

	private byte[] getByteArray(String name)
	{
		String filePath = name.replace(".", File.separator);
		byte[] buf = null;
		try
		{
			FileInputStream in = new FileInputStream(filePath);
			buf = new byte[in.available()];
			in.read(buf);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return buf;
	}

}