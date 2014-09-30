/*
Copyright (c) 2011, Catholic University of America.
Developed with the sponsorship of the Defense Advanced Research Projects Agency (DARPA).
 
Permission is hereby granted, free of charge, to any person obtaining a copy of this data, including any software or models in source or binary form, specifications, algorithms, and documentation (collectively “the Data”), to deal in the Data without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Data, and to permit persons to whom the Data is furnished to do so, subject to the following conditions:
 
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Data.
 
THE DATA IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS, SPONSORS, DEVELOPERS, CONTRIBUTORS, OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE DATA OR THE USE OR OTHER DEALINGS IN THE DATA.
*/

package gov.nasa.jsc.mdrules.repository;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.jsc.mdrules.util.Util;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 * Methods for reading statements from a Sesame repository
 * 
 * @author Sidney Bailin
 * 
 */

public class ReadSesame {

	/**
	 * Get statements using an existing repository connection
	 * @param repConn Repository connection
	 * @param subj Statement subject (null is wild card)
	 * @param pred Statement predicate (null is wild card)
	 * @param obj Statement object (null is wild card)
	 * @param infer If true, inferred statements will be included
	 * @param contexts The graphs to read from
	 */
	static public List<Statement> getFromRepository(RepositoryConnection repConn, Resource subj, URI pred, Value obj, boolean infer, Resource... contexts) {
		List<Statement> stmts = new ArrayList<Statement>();
		RepositoryResult<Statement> res = null;
		try {
			res = repConn.getStatements(subj, pred, obj, infer, contexts);
			while (res.hasNext()) {
				stmts.add(res.next());
			}
		} 
		catch (RepositoryException e) {
			Util.logException(e, ReadSesame.class);
		}
		return stmts;
	}

	
	/**
	 * Get statements creating a temporary repository connection
	 * @param repConn Repository connection
	 * @param subj Statement subject (null is wild card)
	 * @param pred Statement predicate (null is wild card)
	 * @param obj Statement object (null is wild card)
	 * @param infer If true, inferred statements will be included
	 * @param contexts The graphs to read from
	 */
	static public List<Statement> getFromRepository(Repository rep, Resource subj, URI pred, Value obj, boolean infer, Resource... contexts) {
		List<Statement> stmts = new ArrayList<Statement>();
		
		RepositoryResult<Statement> res = null;
		try {
			RepositoryConnection repConn = rep.getConnection();

			res = repConn.getStatements(subj, pred, obj, infer, contexts);				
			repConn.close();
			while (res.hasNext()) {
				stmts.add(res.next());
			}
		} 
		catch (RepositoryException e) {
			Util.logException(e, ReadSesame.class);
		}
		return stmts;
	}
	

}
