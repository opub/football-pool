package com.toconnor.pool.page.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.toconnor.pool.util.SparseArrayList;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.Not;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.VerticalLayout;

/**
 * SortableLayout
 */
public class SortableLayout extends CustomComponent
{
    public static interface ISortListener
    {
        void handleSortEvent(List<Component> components);
    }

    private final AbstractOrderedLayout layout = new VerticalLayout();  //only support vertical sorting
    private final DropHandler dropHandler;

	public SortableLayout(ISortListener listener)
    {
        addStyleName("no-horizontal-drag-hints");
        addStyleName("no-box-drag-hints");

        dropHandler = new ReorderLayoutDropHandler(layout, listener);

        DragAndDropWrapper pane = new DragAndDropWrapper(layout);
        setCompositionRoot(pane);
    }

    public void addComponent(Component component, boolean draggable)
    {
        WrappedComponent wrapper = new WrappedComponent(component, dropHandler, draggable);
        wrapper.setSizeUndefined();
        component.setWidth("100%");
        wrapper.setWidth("100%");
        layout.addComponent(wrapper);
    }

	public void removeAllComponents()
	{
		layout.removeAllComponents();
	}

	public List<Component> getComponents()
	{
		List<Component> components = new ArrayList<Component>();

		Iterator<Component> wraps = layout.iterator();
		while(wraps.hasNext())
		{
			components.add(((WrappedComponent)wraps.next()).content);
		}

		return components;
	}

    public static class WrappedComponent extends DragAndDropWrapper
    {
        private final Component content;
        private final DropHandler dropHandler;
	    private final boolean locked;

        public WrappedComponent(Component content, DropHandler dropHandler, boolean draggable)
        {
            super(content);
            this.content = content;
            this.dropHandler = dropHandler;
	        this.locked = !draggable;
            setDragStartMode(draggable ? DragStartMode.WRAPPER : DragStartMode.NONE);
        }

        @Override
        public DropHandler getDropHandler()
        {
            return dropHandler;
        }

        public Component getContent()
        {
            return content;
        }

	    public boolean getLocked()
	    {
		    return locked;
	    }
    }

    private static class ReorderLayoutDropHandler implements DropHandler
    {
        private AbstractOrderedLayout layout;
        private ISortListener listener;

        public ReorderLayoutDropHandler(AbstractOrderedLayout layout, ISortListener listener)
        {
            this.layout = layout;
            this.listener = listener;
        }

        public AcceptCriterion getAcceptCriterion()
        {
            return new Not(AbstractSelect.VerticalLocationIs.MIDDLE);   //new Not(SourceIsTarget.get());
        }

        public void drop(DragAndDropEvent dropEvent)
        {
            Transferable transferable = dropEvent.getTransferable();
            Component sourceComponent = transferable.getSourceComponent();
            if (sourceComponent instanceof WrappedComponent)
            {
                TargetDetails dropTargetData = dropEvent.getTargetDetails();
                DropTarget target = dropTargetData.getTarget();

	            //locked items get stored off into a sparse list with their fixed indexes and then added back
	            SparseArrayList<WrappedComponent> lockedItems = new SparseArrayList<WrappedComponent>();
	            ArrayList<WrappedComponent> sortedItems = new ArrayList<WrappedComponent>();

				boolean dropped = false;
	            int index = 0;

	            Iterator<Component> componentIterator = layout.iterator();
	            while (componentIterator.hasNext())
	            {
		            WrappedComponent next = (WrappedComponent)componentIterator.next();

		            // save off locked item before anything else so its index is clean
		            if(next.getLocked())
		            {
			            lockedItems.add(index, next);
		            }
					index++;

		            if(next != sourceComponent)
		            {
			            if(next == target)
			            {
				            dropped = true;

				            // add source to list relative to target
				            if(next.getLocked())
				            {
					            sortedItems.add((WrappedComponent)sourceComponent);
				            }
				            else if(dropTargetData.getData("verticalLocation").equals(VerticalDropLocation.TOP.toString()))
				            {
					            sortedItems.add((WrappedComponent)sourceComponent);
					            sortedItems.add(next);
				            }
				            else
				            {
					            sortedItems.add(next);
					            sortedItems.add((WrappedComponent)sourceComponent);
				            }
			            }
			            else if(!next.getLocked())
			            {
				            sortedItems.add(next);
			            }
		            }
	            }

                if (!dropped)
                {
                    // target not found - if dragging from another layout
                    return;
                }

	            // insert locked items back into correct positions
	            for(int i=0; i < lockedItems.size(); i++)
	            {
		            WrappedComponent item = lockedItems.get(i);
		            if(item != null)
		            {
			            sortedItems.add(i, item);
		            }
	            }

                // reset layout with new order
                layout.removeAllComponents();
                layout.addComponents(sortedItems.toArray(new WrappedComponent[sortedItems.size()]));

                // get list of original components in new order
                List<Component> sorted = new ArrayList<Component>();
                Iterator<Component> items = layout.iterator();
                while(items.hasNext())
                {
                    WrappedComponent wrapped = (WrappedComponent)items.next();
                    sorted.add(wrapped.getContent());
                }
                listener.handleSortEvent(sorted);
            }
        }
    }
}
