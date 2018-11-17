package Indexing.Index;

/**
 * represents an index entry.
 * The main difference from PermanentIndexEntry is that it holds the Posting for the term in main memory, and is meant to be
 * written onto disk (only depositing the postings)
 */
public class TemporaryIndexEntry extends IndexEntry {
}
