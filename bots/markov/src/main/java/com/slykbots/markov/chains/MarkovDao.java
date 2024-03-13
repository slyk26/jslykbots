package com.slykbots.markov.chains;

import com.slykbots.components.db.DB;
import com.slykbots.components.db.dao.Dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MarkovDao extends Dao<MarkovEntry> {

    private final Random r = new Random();

    @Override
    public int create(MarkovEntry m) {
        String sql = "insert into markov_data(guild_id, current_word, next_word, frequency) values(?,?,?,?) returning id";
        try (var c = DB.connect(); var stmt = Objects.requireNonNull(c).prepareStatement(sql)) {
            stmt.setString(1, m.getGuildId());
            stmt.setString(2, m.getCurrentWord());
            stmt.setString(3, m.getNextWord());
            stmt.setInt(4, m.getFrequency());
            var rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException | NullPointerException e) {
            logger.error("Cannot insert MarkovEntry {}: {}", m, e.getMessage());
            return -1;
        }
    }

    @Override
    public MarkovEntry read(int id) {
        String sql = "select id, guild_id, current_word, next_word, frequency from markov_data where id = ?";
        try (var c = DB.connect(); var stmt = Objects.requireNonNull(c).prepareStatement(sql)) {
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            return this.mapSingleRs(rs);
        } catch (SQLException | NullPointerException e) {
            logger.error("Cannot read MarkovEntry {}: {}", id, e.getMessage());
            return null;
        }
    }

    @Override
    public void update(MarkovEntry m) {
        String sql = "update markov_data set guild_id = ?, current_word = ?, next_word = ?, frequency = ? where id = ?";
        try (var c = DB.connect(); var stmt = Objects.requireNonNull(c).prepareStatement(sql)) {
            stmt.setString(1, m.getGuildId());
            stmt.setString(2, m.getCurrentWord());
            stmt.setString(3, m.getNextWord());
            stmt.setInt(4, m.getFrequency());
            stmt.setInt(5, m.getId());
            if (stmt.executeUpdate() > 1)
                logger.warn("More than one entry has been updated: {}", m);
        } catch (SQLException | NullPointerException e) {
            logger.error("Cannot update MarkovEntry {}: {}", m, e.getMessage());
        }
    }

    @Override
    public MarkovEntry delete(int id) {
        String sql = "delete from markov_data where id = ? returning id, guild_id, current_word, next_word, frequency";
        try (var c = DB.connect(); var stmt = Objects.requireNonNull(c).prepareStatement(sql)) {
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            return this.mapSingleRs(rs);
        } catch (SQLException | NullPointerException e) {
            logger.error("Cannot delete MarkovEntry {}: {}", id, e.getMessage());
            return null;
        }
    }

    @Override
    public List<MarkovEntry> list() {
        String sql = "select id, guild_id, current_word, next_word, frequency from markov_data";
        try (var c = DB.connect(); var stmt = Objects.requireNonNull(c).prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            return this.mapRs(rs);
        } catch (SQLException | NullPointerException e) {
            logger.error("Cannot read all MarkovEntries: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    protected MarkovEntry mapSingleRs(ResultSet rs) throws SQLException {
        MarkovEntry m = new MarkovEntry();
        m.setId(rs.getInt(1));
        m.setGuildId(rs.getString(2));
        m.setCurrentWord(rs.getString(3));
        m.setNextWord(rs.getString(4));
        m.setFrequency(rs.getInt(5));
        return m;
    }

    public MarkovEntry getExistingCombination(String currentWord, String nextWord, String guildId) {
        String comparison = nextWord != null ? "= ?" : "is null";
        String sql = "select id, guild_id, current_word, next_word, frequency from markov_data where current_word = ? and next_word " + comparison + " and guild_id = ?";
        logger.debug("{}, {}, {}", currentWord, nextWord, guildId);
        MarkovEntry retVal = null;
        try (var c = DB.connect(); var stmt = Objects.requireNonNull(c).prepareStatement(sql)) {
            stmt.setString(1, currentWord);
            if (nextWord != null)
                stmt.setString(2, nextWord);
            stmt.setString(nextWord == null ? 2 : 3, guildId);
            var rs = stmt.executeQuery();

            if (rs.isBeforeFirst()) {
                rs.next();
                retVal = this.mapSingleRs(rs);
            }
        } catch (SQLException | NullPointerException e) {
            logger.error("Cannot check Combination : {}", e.getMessage());
        }
        return retVal;
    }

    public int getCount(String guildId) {
        String sql = "select count(*) from markov_data where guild_id = ?";
        int retVal = -1;
        try (var c = DB.connect(); var stmt = Objects.requireNonNull(c).prepareStatement(sql)) {
            stmt.setString(1, guildId);
            var rs = stmt.executeQuery();
            rs.next();
            retVal = rs.getInt(1);
        } catch (SQLException e) {
            logger.error("Cannot get count: {}", e.getMessage());
        }
        return retVal;
    }

    public MarkovEntry getRandom(String guildId) {
        String sql = "select id, guild_id, current_word, next_word, frequency from markov_data where guild_id = ? order by random() limit 1";
        MarkovEntry retVal = null;
        try (var c = DB.connect(); var stmt = Objects.requireNonNull(c).prepareStatement(sql)) {
            stmt.setString(1, guildId);
            var rs = stmt.executeQuery();
            rs.next();
            retVal = this.mapSingleRs(rs);
            logger.debug("getRandom: {}", retVal);
        } catch (SQLException e) {
            logger.error("Cannot get a Starting Entry: {}", e.getMessage());
        }
        return retVal;
    }

    public MarkovEntry getNext(String newCurrentWord, String guildId) {
        String sql = "select id, guild_id, current_word, next_word, frequency from markov_data where current_word = ? and guild_id = ? order by frequency desc";
        MarkovEntry retVal = null;
        try (var c = DB.connect(); var stmt = Objects.requireNonNull(c).prepareStatement(sql)) {
            stmt.setString(1, newCurrentWord);
            stmt.setString(2, guildId);
            var rs = stmt.executeQuery();
            var ms = this.mapRs(rs);
            logger.debug("{}", ms);
            var freqs = ms.stream().map(MarkovEntry::getFrequency).mapToInt(i -> i).sum();
            logger.debug("freqs: {}", freqs);
            retVal = findNext(ms, r.nextInt(freqs));
            logger.debug("getNext: {}", retVal);

        } catch (SQLException e) {
            logger.error("Cannot get a Starting Entry: {}", e.getMessage());
        }
        return retVal;
    }

    private MarkovEntry findNext(List<MarkovEntry> l, int freqs) {
        var sum = 0;

        logger.debug("sum start {}, freqs {}", sum, freqs);

        for (MarkovEntry e : l) {
            if (sum + e.getFrequency() > freqs) {
                return e;
            }
        }
        return null;
    }
}
