package model.cards;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> deck;
    private boolean garbage;
    private ArrayList<Card> discard;

    /**
     * Create a new empty deck
     */
    public Deck() {
        this(false);
    }

    /**
     * Create a new empty deck. If <code>garbage</code> is true, it also creates an empty discarded card deck
     *
     * @param garbage if true set the discarded card deck
     */
    public Deck(boolean garbage) {
        deck = new ArrayList<>();
        if(garbage) discard = new ArrayList<>();
        this.garbage = garbage;
    }

    /**
     * Empty the decks
     */
    public void flush() {
        deck.clear();
        if(garbage) discard.clear();
    }

    /**
     * Move the discarded cards into the deck and shuffle it
     */
    public void shuffle() {
        if(garbage) {
            deck.addAll(discard);
            discard.clear();
        }
        Collections.shuffle(deck);
    }

    /**
     * Add a <code>card</code> to the deck
     *
     * @param card the <code>card</code> to add
     */
    public void addCard(Card card) {
        if(card == null) throw new NullPointerException("card cannot be null");
        deck.add(card);
    }

    /**
     * Draw the top card from the deck, return it and remove it from the deck
     *
     * @return <code>null</code> if the deck is empty or if all the cards have been discarded, otherwise returns the top card
     */
    public Card draw() {
        try {
            return deck.remove(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * if garbage is <code>false</code> it inserts at the bottom of the deck the <code>card</code>, otherwise it inserts it on the top of <code>discard</code>
     *
     * @param card the card to be discarded
     */
    public void discardCard(Card card) {
        if(card == null) throw new NullPointerException("card cannot be null");

        if(garbage) discard.add(0, card);
        else deck.add(card);
    }
}
