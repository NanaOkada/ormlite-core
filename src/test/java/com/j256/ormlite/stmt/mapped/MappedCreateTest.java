package com.j256.ormlite.stmt.mapped;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.sql.SQLException;

import org.junit.Test;

import com.j256.ormlite.BaseOrmLiteCoreTest;
import com.j256.ormlite.db.BaseDatabaseType;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.stmt.StatementExecutor;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableInfo;

public class MappedCreateTest extends BaseOrmLiteCoreTest {

	@Test
	public void testGeneratedIdSequence() throws Exception {
		DatabaseType databaseType = new NeedsSequenceDatabaseType();

		TableInfo<GeneratedId> tableInfo = new TableInfo<GeneratedId>(databaseType, GeneratedId.class);
		StatementExecutor<GeneratedId, String> se = new StatementExecutor<GeneratedId, String>(databaseType, tableInfo);
		DatabaseConnection databaseConnection = createMock(DatabaseConnection.class);
		expect(databaseConnection.queryForLong(isA(String.class))).andReturn(1L);
		expect(databaseConnection.insert(isA(String.class), isA(Object[].class), isA(SqlType[].class))).andReturn(1);

		replay(databaseConnection);
		GeneratedId genIdSeq = new GeneratedId();
		se.create(databaseConnection, genIdSeq);
		verify(databaseConnection);
	}

	@Test
	public void testGeneratedIdSequenceLong() throws Exception {
		DatabaseType databaseType = new NeedsSequenceDatabaseType();

		StatementExecutor<GeneratedIdLong, String> se =
				new StatementExecutor<GeneratedIdLong, String>(databaseType, new TableInfo<GeneratedIdLong>(
						databaseType, GeneratedIdLong.class));
		DatabaseConnection databaseConnection = createMock(DatabaseConnection.class);
		expect(databaseConnection.queryForLong(isA(String.class))).andReturn(1L);
		expect(databaseConnection.insert(isA(String.class), isA(Object[].class), isA(SqlType[].class))).andReturn(1);

		replay(databaseConnection);
		GeneratedIdLong genIdSeq = new GeneratedIdLong();
		se.create(databaseConnection, genIdSeq);
		verify(databaseConnection);
	}

	@Test
	public void testNoCreateSequence() throws Exception {
		MappedCreate.build(databaseType, new TableInfo<GeneratedId>(databaseType, GeneratedId.class));
	}

	@Test(expected = SQLException.class)
	public void testSequenceZero() throws Exception {
		DatabaseConnection databaseConnection = createMock(DatabaseConnection.class);
		expect(databaseConnection.queryForLong(isA(String.class))).andReturn(0L);
		replay(databaseConnection);
		MappedCreate<GeneratedIdSequence> mappedCreate =
				MappedCreate.build(databaseType, new TableInfo<GeneratedIdSequence>(databaseType,
						GeneratedIdSequence.class));
		mappedCreate.insert(databaseConnection, new GeneratedIdSequence());
		verify(databaseConnection);
	}

	protected static class GeneratedId {
		@DatabaseField(generatedId = true)
		public int genId;
		@DatabaseField
		public String stuff;
	}

	protected static class GeneratedIdLong {
		@DatabaseField(generatedId = true)
		long id;
		@DatabaseField
		public String stuff;
	}

	protected static class GeneratedIdSequence {
		@DatabaseField(generatedIdSequence = "seq")
		int id;
		@DatabaseField
		public String stuff;
	}

	// for testing reserved words as field names
	protected static class ReservedField {
		@DatabaseField(generatedId = true)
		public int select;
		@DatabaseField
		public String from;
		@DatabaseField
		public String table;
		@DatabaseField
		public String where;
		@DatabaseField
		public String group;
		@DatabaseField
		public String order;
		@DatabaseField
		public String values;
	}

	protected static class JustId {
		@DatabaseField(generatedId = true)
		int id;
	}

	private class NeedsSequenceDatabaseType extends BaseDatabaseType {
		public String getDriverClassName() {
			return "foo.bar.baz";
		}
		public String getDriverUrlPart() {
			return "foo";
		}
		@Override
		public boolean isIdSequenceNeeded() {
			return true;
		}
	}
}