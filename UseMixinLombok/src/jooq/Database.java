package jooq;

import lombok.Mixin;

@Mixin
interface Database {
	String select();
	Database select(String select);
	String from();
	Database from(String from);
	String where();
	Database where(String where);
	default Database runTest() {
		return Database.of("", "", "")
					   .select("a, b")
					   .from("Table")
					   .where("c > 10");
	}
}

@Mixin
interface ExtendedDatabase extends Database {
	String orderBy();
	ExtendedDatabase orderBy(String orderBy);
	default ExtendedDatabase runTest2() {
		return ExtendedDatabase.of("", "", "", "")
							   .select("a, b")
							   .from("Table")
							   .where("c > 10")
							   .orderBy("b");
	}
}