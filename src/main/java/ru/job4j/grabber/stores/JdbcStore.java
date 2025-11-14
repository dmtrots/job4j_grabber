package ru.job4j.grabber.stores;

import ru.job4j.grabber.model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStore implements Store {
    private final Connection connection;

    public JdbcStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Post post) {
        String sql = """
                INSERT INTO post(title, link, description, time)
                VALUES (?, ?, ?, ?)
                RETURNING id;
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getLink());
            ps.setString(3, post.getDescription());
            ps.setLong(4, post.getTime());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    post.setId(rs.getLong("id"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> list = new ArrayList<>();

        String sql = "SELECT * FROM post";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(createPost(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public Optional<Post> findById(Long id) {
        String sql = "SELECT * FROM post WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createPost(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    private Post createPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setLink(rs.getString("link"));
        post.setDescription(rs.getString("description"));
        post.setTime(rs.getLong("time"));
        return post;
    }
}