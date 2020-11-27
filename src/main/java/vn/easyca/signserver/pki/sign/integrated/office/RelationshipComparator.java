package vn.easyca.signserver.pki.sign.integrated.office;

import org.w3c.dom.Element;

import java.util.Comparator;

public class RelationshipComparator
        implements Comparator<Element> {

    @Override
    public int compare(Element element1, Element element2) {
        String id1 = element1.getAttribute("Id");
        String id2 = element2.getAttribute("Id");
        return id1.compareTo(id2);
    }
}
