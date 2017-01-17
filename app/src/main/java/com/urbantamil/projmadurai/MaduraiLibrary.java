/*
This code is released under MIT License
(C) 2016-2017, Ezhil Language Foundation
<ezhillang@gmail.com>
*/
package com.urbantamil.projmadurai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by muthu on 3/13/2016.
 */
public class MaduraiLibrary {
    private ArrayList<MaduraiBook> book_list;
    private Set<String> genre_list;
    private Set<String> author_list;
    private HashMap<String,ArrayList<MaduraiBook> > genre_to_book;
    private HashMap<String,ArrayList<MaduraiBook> > author_to_book;
    private HashMap<String,MaduraiBook> title_to_book;

    /// c-tor for whole data model of Project Madurai Library
    public MaduraiLibrary() {
        book_list = new ArrayList<MaduraiBook>();
        genre_list = new HashSet<String>();
        author_list = new HashSet<String>();
        genre_to_book = new HashMap<String, ArrayList<MaduraiBook>>();
        author_to_book = new HashMap<String, ArrayList<MaduraiBook>>();
        title_to_book = new HashMap<String, MaduraiBook>();
    }

    public ArrayList<MaduraiBook> getBooksForGenre(String genre) {
        return genre_to_book.get(genre);
    }

    public ArrayList<MaduraiBook> getBooksForAuthor(String author) {
        return author_to_book.get(author);
    }

    public MaduraiBook getBookForTitle(String title) {
        return title_to_book.get(title);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Books => "+book_list.size());
        sb.append("\n# Authors => "+author_list.size());
        sb.append("\n# Genres => "+genre_list.size());
        sb.append("\n# Books - Authors => "+author_to_book.size());
        sb.append("\n# Books - Genres => "+genre_to_book.size());
        sb.append("\n# Title - Authors => "+title_to_book.size());
        return sb.toString();
    }

    /// add book
    public void addBook(MaduraiBook book) {
        book_list.add(book);
        addAuthor(book.getAuthor());
        addGenre(book.getGenre());
        title_to_book.put(book.getTitle(), book);

        ArrayList<MaduraiBook> g_mb = genre_to_book.get(book.getGenre());
        g_mb.add(book);
        genre_to_book.put(book.getGenre(), g_mb);

        ArrayList<MaduraiBook> a_mb = author_to_book.get(book.getAuthor());
        a_mb.add(book);
        author_to_book.put(book.getAuthor(), a_mb);
    }

    public int size() {
        return book_list.size();
    }

    public MaduraiBook getBookAt(int pos) {
        return book_list.get(pos);
    }

    /// iterators ///
    public Iterator<String> getAuthors() {
        return author_list.iterator();
    }

    public Iterator<String> getGenres() {
        return genre_list.iterator();
    }

    public ArrayList<String> getAuthorsList() {
        return iteratorToList(getAuthors());
    }

    public ArrayList<String> getGenresList() {
        return iteratorToList(getGenres());
    }

    private ArrayList<String>  iteratorToList(Iterator<String> itr) {
        ArrayList<String> c_list = new ArrayList<String>();
        while( itr.hasNext()) {
            c_list.add(itr.next());
        }
        return c_list;
    }

    public ArrayList<String> getTitlesList() {
        ArrayList<String> title_list = new ArrayList<String>();
        Iterator<MaduraiBook> bk_itr = getBooks();
        while(bk_itr.hasNext()) {
            title_list.add(bk_itr.next().getTitle());
        }
        return title_list;
    }

    public ArrayList<MaduraiBook> getBookList() {
        return book_list;
    }

    public Iterator<MaduraiBook> getBooks() {
        return book_list.iterator();
    }



    /// private - add author/genre methods

    private void addAuthor(String author) {
        if ( author.isEmpty() ) {
            author = "Unknown";
        }

        if ( !author_list.contains(author) ) {
            author_list.add(author);
            author_to_book.put(author,new ArrayList<MaduraiBook>());
        }
    }

    private void addGenre(String genre) {
        if ( genre.isEmpty() ) {
            genre = "Uncategorized";
        }

        if ( !genre_list.contains(genre) ) {
            genre_list.add(genre);
            genre_to_book.put(genre, new ArrayList<MaduraiBook>());
        }
    }

}
