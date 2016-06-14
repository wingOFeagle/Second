package test.test_module;

public class TestClassLoaderDemo
{

	// TODO Auto-generated method stub
	public static void main(String[] args) throws InstantiationException,IllegalAccessException
	{
		Class thisCls = TestClassLoaderDemo.class;
		MyClassLoader myClassLoader = new MyClassLoader();
		System.out.println(thisCls.getClassLoader());
		System.out.println(myClassLoader.getParent());
		try
		{
			// 用自定义的类装载器来装载类,这是动态扩展的一种途径
			Class cls2 = myClassLoader.loadClass("test.test_module.TestBeLoader");
			System.out.println(cls2.getClassLoader());
			TestBeLoader test = (TestBeLoader) cls2.newInstance();
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

}
