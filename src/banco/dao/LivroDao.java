package banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import banco.modelo.Autor;
import banco.modelo.Livro;

public class LivroDao implements Dao<Livro> {
	
	private static final String GET_BY_ID = "SELECT * FROM livro NATURAL JOIN autor WHERE id = ?";
	private static final String GET_ALL = "SELECT * FROM livro NATURAL JOIN autor";
	private static final String INSERT = "INSERT INTO livro (titulo, anoPublicacao, editora, autor) "
			+ "VALUES (?, ?, ?, ?)";
	private static final String UPDATE = "UPDATE livro SET titulo = ?, anoPublicacao = ?, editora = ?, "
			+ "autor_id = ? WHERE id = ?";
	private static final String DELETE = "DELETE FROM livro WHERE id = ?";
	
	
	public LivroDao() {
		try {
			createTable();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createTable() throws SQLException {
	    final String sqlCreate = "CREATE TABLE IF NOT EXISTS livro"
	            + "  (id           	INTEGER,"
	            + "   titulo       	VARCHAR(50),"
	            + "   anoPublicacao INTEGER,"
	            + "   editora	  	VARCHAR(50),"
	            + "   autor_id	 		INTEGER,"
	            + "   FOREIGN KEY (autor_id) REFERENCES autor(id),"
	            + "   PRIMARY KEY (id))";
	    
	    	    
	    Connection conn = DbConnection.getConnection();

	    Statement stmt = conn.createStatement();
	    stmt.execute(sqlCreate);
	}
	
	
	private Livro getContaFromRS(ResultSet rs) throws SQLException
    {
		Livro livro = new Livro();
			
		livro.setId( rs.getInt("id") );
		livro.setTitulo( rs.getString("titulo"));
		livro.setAnoPublicacao( rs.getInt("anoPublicacao"));
		livro.setEditora( rs.getString("editora"));
		livro.setAutor( new Autor(rs.getInt("autor_id"), rs.getString("nome"), rs.getLong("cpf")));

		return livro;
    }
	
	
	@Override
	public Livro getByKey(int id) {
		Connection conn = DbConnection.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		Livro livro = null;
		
		try {
			stmt = conn.prepareStatement(GET_BY_ID);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				livro = getContaFromRS(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}
		
		return livro;
	}

	@Override
	public List<Livro> getAll() {
		Connection conn = DbConnection.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		
		List<Livro> livro = new ArrayList<>();
		
		try {
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery(GET_ALL);
			
			while (rs.next()) {
				livro.add(getContaFromRS(rs));
			}			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}
		
		return livro;
	}

	@Override
	public void insert(Livro livro) {
		Connection conn = DbConnection.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		
		try {
			stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, livro.getTitulo());
			stmt.setInt(2, livro.getAnoPublicacao());
			stmt.setString(3, livro.getEditora());
			stmt.setString(4, livro.getAutor().getNome());
			
			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();
			
			if (rs.next()) {
				livro.setId(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

	}

	@Override
	public void delete(int id) {
		Connection conn = DbConnection.getConnection();
		
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(DELETE);
			
			stmt.setInt(1, id);
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, null);
		}
	}

	@Override
	public void update(Livro livro) {
		Connection conn = DbConnection.getConnection();
		
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(UPDATE);
			stmt.setString(1, livro.getTitulo());
			stmt.setInt(2, livro.getAnoPublicacao());
			stmt.setString(3, livro.getEditora());
			stmt.setString(4, livro.getAutor().getNome());
			
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close (conn, stmt, null);
		}
	}
	
	private static void close(Connection myConn, Statement myStmt, ResultSet myRs) {
		try {
			if (myRs != null) {
				myRs.close();
			}
			
			if (myStmt != null) {
				myStmt.close();
			}
			
			if (myConn != null) {
				myConn.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao fechar recursos.", e);
		}
		
	}

}
