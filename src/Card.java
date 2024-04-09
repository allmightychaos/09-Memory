import javax.swing.*;

public class Card extends JButton {
    private Icon faceIcon;
    private Icon backIcon;
    private boolean isFlipped;

    public Card(Icon faceIcon, Icon backIcon) {
        this.faceIcon = faceIcon;
        this.backIcon = backIcon;
        this.isFlipped = false;
        setIcon(backIcon);
    }

    public void flipCard() {
        if (isFlipped) {
            setIcon(backIcon);
        } else {
            setIcon(faceIcon);
        }
        isFlipped = !isFlipped;
    }

    public boolean isMatching(Card otherCard) {
        return faceIcon.equals(otherCard.faceIcon);
    }

    public boolean isFlipped() {
        return isFlipped;
    }
}
