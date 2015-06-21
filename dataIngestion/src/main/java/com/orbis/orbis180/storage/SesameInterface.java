package com.orbis.orbis180.storage;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.manager.RemoteRepositoryManager;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.config.SailImplConfig;
import org.openrdf.sail.nativerdf.config.NativeStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>SesameInterface</code> accesses OpenRDF Sesame server operations.
 *
 * @author Carlos J. Andino <candino@orbistechnologies.com>
 */
public class SesameInterface {

	final static protected Logger logger = LoggerFactory.getLogger("command");
	private String server;
	private String repositoryID;
	private RemoteRepositoryManager manager;
	private Repository repository;
	private String context;
	private String baseURI;

	public SesameInterface() {
		initialize();
	}

	private void initialize() {
		Properties config = new Properties();
		try {
			config.load(getClass().getResourceAsStream("/conf/config.properties"));

			// Obtain session info
			server = config.getProperty("com.orbis.orbis180.sesame.server");
//            repositoryID = config.getProperty("com.orbis.orbis180.sesame.repository");
			context = config.getProperty("com.orbis.orbis180.sesame.context");
			baseURI = config.getProperty("com.orbis.orbis180.sesame.baseuri");

			manager = new RemoteRepositoryManager(server);
			manager.initialize();

		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
		} catch (RepositoryException e) {
                    logger.error("Could not initialize the Repository Manager for {}.\n{}", server, e.getLocalizedMessage());
            }
	}

	public void createRepository(String repoName) {
		try {
			// create a configuration for the SAIL stack
			repositoryID = manager.getNewRepositoryID(repoName);
			SailImplConfig backendConfig = new NativeStoreConfig("spoc,posc,ospc", true);

			// create a configuration for the repository implementation
			RepositoryImplConfig repositoryTypeSpec = new SailRepositoryConfig(backendConfig);
			RepositoryConfig repoConfig = new RepositoryConfig(repositoryID, repositoryTypeSpec);
			// repoConfig.setTitle("Test repository for SemBio IRAD.");
			manager.addRepositoryConfig(repoConfig);
			repository = manager.getRepository(repositoryID);
			repository.initialize();
			
			//config.validate();
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage());
		}
		enableCurrentRepository();
	}
	
	public boolean repositoryExist(String repository)
	{
		try {
			Repository repo = manager.getRepository(repository);
			if (repo != null)
				return true;			
		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage());
		}
		return false;
	}

	public void openRepository(String repoName) {

		repositoryID = repoName;
		enableCurrentRepository();
	}

	private void enableCurrentRepository() {
		try {
			SailImplConfig backendConfig = new NativeStoreConfig("spoc,posc", true);

			// create a configuration for the repository implementation
			RepositoryImplConfig repositoryTypeSpec = new SailRepositoryConfig(backendConfig);
			RepositoryConfig repoConfig = new RepositoryConfig(repositoryID, repositoryTypeSpec);
			manager.addRepositoryConfig(repoConfig);
			repository = manager.getRepository(repositoryID);
			repository.initialize();
			Set<String> repos = manager.getRepositoryIDs();
			for (String repo : repos) {
				logger.debug("{} repository is available.", repo.toString());
			}
			//config.validate();

		} catch (Exception ex) {
			logger.error(ex.getLocalizedMessage());
		}
	}

	public void storeTriple(URI subject, URI predicate, Value object) {

		try {
			RepositoryConnection con = repository.getConnection();
			try {
				con.add(subject, predicate, object, repository.getValueFactory().createURI(context));
			} finally {
				con.close();
			}
		} catch (RepositoryException ex) {
			logger.error(ex.getLocalizedMessage());
		}

	}

	public String getBaseURI(String humanFriendlyName, String backupName) {

		String uri = baseURI;

		try {

			String encoded = URLEncoder.encode(humanFriendlyName.replace(" ", "_"), "UTF-8");

			uri += "/" + encoded;

		} catch (UnsupportedEncodingException ex) {

			logger.error("Couldn't create a URI from human friendly name: " + humanFriendlyName, ex);

			uri += "/" + backupName;
		}

		return uri;
	}

	public void printStats() {
		System.out.println("Sesame Server: " + server);
		System.out.println("Repository: " + repositoryID);

	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setRepositoryID(String repositoryID) {
		this.repositoryID = repositoryID;
	}

	public String getRepositoryID() {
		return repositoryID;
	}

	public RemoteRepositoryManager getManager() {
		return manager;
	}

	public String getContext() {
		return context;
	}

	public Repository getRepository() {
		return repository;
	}

	public ValueFactory getValueFactory() {
		return repository.getValueFactory();
	}
}
