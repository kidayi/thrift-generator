package generator;

import com.sohu.thrift.generator.builder.ThriftFileBuilder;

public class Main {
	public static void main(String[] args) {
		ThriftFileBuilder fileBuilder = new ThriftFileBuilder();
		try {
			fileBuilder.buildToOutputStream(MethodDefinition.class, System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
