/*
Copyright (c) 2011, Catholic University of America.
Developed with the sponsorship of the Defense Advanced Research Projects Agency (DARPA).
 
Permission is hereby granted, free of charge, to any person obtaining a copy of this data, including any software or models in source or binary form, specifications, algorithms, and documentation (collectively “the Data”), to deal in the Data without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Data, and to permit persons to whom the Data is furnished to do so, subject to the following conditions:
 
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Data.
 
THE DATA IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS, SPONSORS, DEVELOPERS, CONTRIBUTORS, OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE DATA OR THE USE OR OTHER DEALINGS IN THE DATA.
*/

package gov.nasa.jsc.mdrules.repository;

import gov.nasa.jsc.mdrules.util.Util;

import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;


/**
 * Methods for writing to a Sesame repository. This includes both adding and
 * removing statements.
 * 
 * @author Sidney Bailin
 * 
 */
public class WriteSesame {

	/**
	 * Add statements using an existing repository connection
	 * 
	 * @param repConn
	 *            Repository connection
	 * @param stmts
	 *            Statements to be added
	 * @param commit
	 *            If true, modification will be committed
	 * @contexts contexts to add the statements to
	 */
	static public void addToRepository(RepositoryConnection repConn,
			List<Statement> stmts, boolean commit, Resource... contexts) {
		try {
			repConn.add(stmts, contexts);
			if (commit) {
				repConn.commit();
			}
		} 
		catch (RepositoryException e) {
			Util.logException(e,  "edu.nyumc.broker.repository.sesame.WriteSesame");
		}
	}

	/**
	 * Add statements, creating a temporary repository connection
	 * 
	 * @param rep
	 *            Repository
	 * @param stmts
	 *            Statements to be added
	 * @param commit
	 *            If true, modification will be committed
	 * @contexts contexts to add the statements to
	 */
	static public void addToRepository(Repository rep, List<Statement> stmts,
			boolean commit, Resource... contexts) {
		try {
			RepositoryConnection repConn = rep.getConnection();

			repConn.add(stmts, contexts);
			if (commit) {
				repConn.commit();
			}
			if (commit) {
				repConn.commit();
			}
			repConn.close();
		} 
		catch (RepositoryException e) {
			Util.logException(e,  "edu.nyumc.broker.repository.sesame.WriteSesame");
		}
	}

	/**
	 * Remove statements using an existing repository connection
	 * 
	 * @param repConn
	 *            Repository connection
	 * @param stmts
	 *            Statements to be removed
	 * @param commit
	 *            If true, modification will be committed
	 * @contexts contexts to remove the statements from
	 */
	static public void removeFromRepository(RepositoryConnection repConn,
			List<Statement> stmts, boolean commit, Resource... contexts) {
		try {

			repConn.remove(stmts, contexts);
			if (commit) {
				repConn.commit();
			}
			if (commit) {
				repConn.commit();
			}
		} 
		catch (RepositoryException e) {
			Util.logException(e,  "edu.nyumc.broker.repository.sesame.WriteSesame");
		}
	}

	/**
	 * Remove statements, creating a temporary repository connection
	 * 
	 * @param rep
	 *            Repository
	 * @param stmts
	 *            Statements to be removed
	 * @param commit
	 *            If true, modification will be committed
	 * @contexts contexts to remove the statements from
	 */
	static public void removeFromRepository(Repository rep, List<Statement> stmts,
			boolean commit, Resource... contexts) {
		try {
			RepositoryConnection repConn = rep.getConnection();

			repConn.remove(stmts, contexts);
			if (commit) {
				repConn.commit();
			}
			if (commit) {
				repConn.commit();
			}
			repConn.close();
		} 
		catch (RepositoryException e) {
			Util.logException(e,  "edu.nyumc.broker.repository.sesame.WriteSesame");
		}
	}

}
