package com.sohu.thrift.generator.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sohu.thrift.generator.Constants;
import com.sohu.thrift.generator.Generic;
import com.sohu.thrift.generator.ThriftEnum;
import com.sohu.thrift.generator.ThriftEnumField;
import com.sohu.thrift.generator.ThriftField;
import com.sohu.thrift.generator.ThriftStruct;
import com.sohu.thrift.generator.ThriftType;
import com.sohu.thrift.generator.utils.CommonUtils;

public class ThriftStructBuilder {
	
	ThriftFieldBuilder thriftFieldBuilder = new ThriftFieldBuilder();
	
	public void buildThriftStruct(Class<?> clazz, List<ThriftStruct> structs,Map<String,Object> mapStructs,List<ThriftEnum> enums) {
		if(CommonUtils.isBasicType(clazz)||CommonUtils.isCollectionType(clazz)
				|| mapStructs.containsKey(clazz.getName())){
			return;
		}
		mapStructs.put(clazz.getName(), clazz);
		
		List<ThriftField> thriftFields=getFields(clazz, structs,mapStructs, enums);
		
		ThriftStruct struct = new ThriftStruct();
		struct.setName(clazz.getSimpleName()+Constants.end_str);
		struct.setFields(thriftFields);
		structs.add(struct);
		
	}
	
	public List<ThriftField> getFields(Class<?> clazz, List<ThriftStruct> structs,Map<String,Object> mapStructs, List<ThriftEnum> enums){
		List<ThriftField> thriftFields = new ArrayList<ThriftField>();
		Class<?> temp=clazz;
		while(true){
			try{
				Field[] fields =temp.getDeclaredFields();
				for (Field field : fields) {
					ThriftField thriftField = thriftFieldBuilder.buildThriftField(this, field, structs,mapStructs, enums);
					if(thriftField == null) {
						continue;
					}
					thriftFields.add(thriftField);
				}
				temp=temp.getSuperclass();
				if(!temp.getName().endsWith("BaseResult")){
					break;
				}
			}catch(Throwable ex){
				ex.printStackTrace();
			}
		}
		return thriftFields;
	}
	/**
	 * @param structs
	 * @param generic
	 */
	public void buildStrutsByGeneric(List<ThriftStruct> structs,Map<String,Object> mapStructs,
			Generic generic, List<ThriftEnum> enums) {
		List<ThriftType> thriftTypes = generic.getTypes();
		for (ThriftType subThriftType : thriftTypes) {
			if(subThriftType.isStruct()) {
				buildThriftStruct(subThriftType.getJavaClass(), structs,mapStructs, enums);
			}
			if(subThriftType instanceof Generic) {
				this.buildStrutsByGeneric(structs,mapStructs, (Generic) subThriftType, enums);
			}
		}
	}
	
	public ThriftEnum buildThriftEnum(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		ThriftEnum thriftEnum = new ThriftEnum();
		thriftEnum.setName(clazz.getSimpleName());
		
		List<ThriftEnumField> nameValues = new ArrayList<ThriftEnumField>();
		for (int i = 0;i < fields.length;i ++) {
			Field field = fields[i];
			if(field.getName().equals("ENUM$VALUES") || field.getName().equals("__PARANAMER_DATA")) {
				continue;
			}
			ThriftEnumField nameValue = new ThriftEnumField(field.getName(), i);
			nameValues.add(nameValue);
		}
		thriftEnum.setFields(nameValues);
		return thriftEnum;
	}
}
