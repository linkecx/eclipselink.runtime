/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

@SuppressWarnings("nls")
public final class JoinTest extends JPQLParserTest {

	@Test
	public void test_JPQLQuery_Join_01() {

		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN pub.magazines mag WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", join("pub.magazines", "mag")),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_Join_02() {

		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN pub.magazines AS mag WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinAs("pub.magazines", "mag")),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_Join_03() {

		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN pub.magazines WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", join(collectionPath("pub.magazines"), nullExpression())),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_Join_04() {

		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", join(nullExpression(), nullExpression())),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		selectStatement.hasSpaceAfterFrom = false;

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_Join_05() {

		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN AS HAVING pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinAs(nullExpression(), nullExpression())),
			having(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		selectStatement.hasSpaceAfterFrom = false;

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_Join_06() {

		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN AS mag WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinAs(nullExpression(), variable("mag"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_Join_07() {

		String jpqlQuery = "SELECT pub " +
		                   "FROM Publisher pub " +
		                   "     LEFT JOIN pub.magazines mag " +
		                   "WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", leftJoin(collectionPath("pub.magazines"), variable("mag"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_Join_08() {

		String jpqlQuery = "SELECT pub " +
		                   "FROM Publisher pub " +
		                   "     INNER JOIN pub.magazines mag " +
		                   "WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", innerJoin(collectionPath("pub.magazines"), variable("mag"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_Join_09() {

		String jpqlQuery = "SELECT pub " +
		                   "FROM Publisher pub " +
		                   "     LEFT OUTER JOIN pub.magazines mag " +
		                   "WHERE pub.revenue > 1000000";


		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", leftOuterJoin(collectionPath("pub.magazines"), variable("mag"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_Join_10() {

		String jpqlQuery = "Delete from Employee e join e.address a where e.id < 0";

		DeleteStatementTester deleteStatement = deleteStatement(
			delete(
				spacedCollection(
					rangeVariableDeclaration("Employee", "e"),
					bad(join("e.address", "a"))
				)
			),
			where(path("e.id").lowerThan(numeric(0)))
		);

		testInvalidQuery(jpqlQuery, deleteStatement);
	}

	@Test
	public void test_JPQLQuery_JoinFetch_01() {

		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN FETCH pub.magazines mag WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinFetch("pub.magazines", "mag")),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_JoinFetch_02() {

		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN FETCH pub.magazines AS mag WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinFetchAs("pub.magazines", "mag")),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_JoinFetch_03() {

		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN FETCH pub.magazines WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinFetch(collectionPath("pub.magazines"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_JoinFetch_04() {

		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN FETCH WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinFetch(nullExpression())),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		selectStatement.hasSpaceAfterFrom = false;

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_JoinFetch_05() {

		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN FETCH AS HAVING pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinFetchAs(nullExpression(), nullExpression())),
			having(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		selectStatement.hasSpaceAfterFrom = false;

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_JoinFetch_06() {

		String jpqlQuery = "SELECT pub FROM Publisher pub JOIN FETCH AS mag WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinFetchAs(nullExpression(), variable("mag"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_JoinFetch_07() {

		String jpqlQuery = "SELECT pub " +
		                   "FROM Publisher pub " +
		                   "     LEFT JOIN FETCH pub.magazines mag " +
		                   "WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", leftJoinFetch(collectionPath("pub.magazines"), variable("mag"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_JoinFetch_08() {

		String jpqlQuery = "SELECT pub " +
		                   "FROM Publisher pub " +
		                   "     INNER JOIN FETCH pub.magazines mag " +
		                   "WHERE pub.revenue > 1000000";

		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", innerJoinFetch(collectionPath("pub.magazines"), variable("mag"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_JoinFetch_09() {

		String jpqlQuery = "SELECT pub " +
		                   "FROM Publisher pub " +
		                   "     LEFT OUTER JOIN FETCH pub.magazines mag " +
		                   "WHERE pub.revenue > 1000000";


		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", leftOuterJoinFetch(collectionPath("pub.magazines"), variable("mag"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_JoinFetch_10() {

		String jpqlQuery = "SELECT pub " +
		                   "FROM Publisher pub " +
		                   "     LEFT OUTER JOIN FETCH pub.magazines " +
		                   "WHERE pub.revenue > 1000000";


		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", leftOuterJoinFetch(collectionPath("pub.magazines"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_JoinFetch_11() {

		String jpqlQuery = "SELECT pub " +
		                   "FROM Publisher pub " +
		                   "     LEFT JOIN FETCH pub.magazines " +
		                   "WHERE pub.revenue > 1000000";


		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", leftJoinFetch(collectionPath("pub.magazines"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}

	@Test
	public void test_JPQLQuery_JoinFetch_12() {

		String jpqlQuery = "SELECT pub " +
		                   "FROM Publisher pub " +
		                   "     JOIN FETCH pub.magazines " +
		                   "WHERE pub.revenue > 1000000";


		SelectStatementTester selectStatement = selectStatement(
			select(variable("pub")),
			from("Publisher", "pub", joinFetch(collectionPath("pub.magazines"))),
			where(path("pub.revenue").greaterThan(numeric(1000000)))
		);

		testQuery(jpqlQuery, selectStatement);
	}
}