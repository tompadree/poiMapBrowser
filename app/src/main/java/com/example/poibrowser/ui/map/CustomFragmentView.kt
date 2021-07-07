package com.example.poibrowser.ui.map

/**
 * @author Tomislav Curis
 */

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.AsyncListDiffer.ListListener
import com.google.android.gms.maps.GoogleMap
import nz.co.trademe.mapme.annotations.AnnotationFactory
import nz.co.trademe.mapme.annotations.MapAnnotation
import nz.co.trademe.mapme.googlemaps.GoogleMapMeAdapter


/**
     *
     * Advanced users that wish for more control over adapter behavior, or to provide a specific base
     * class should refer to {@link AsyncListDiffer}, which provides custom mapping from diff events
     * to adapter positions.
     *
     * @param <T> Type of the Lists this Adapter will receive.
     */
abstract class MyGoogleMapMeAdapter<T>(context: Context, val diffCallback: DiffUtil.ItemCallback<T>) : GoogleMapMeAdapter(context) {
//    val mDiffer: AsyncListDiffer<T>? = null



    val mDiffer = AsyncListDiffer(
    AdapterListUpdateCallbackCustom(this),
    AsyncDifferConfig.Builder(diffCallback).build()
    )
    private val mListener: ListListener<T> =
        ListListener<T> { previousList, currentList ->
            this.onCurrentListChanged(
                previousList,
                currentList
            )
        }

    /**
     * Submits a new list to be diffed, and displayed.
     *
     *
     * If a list is already being displayed, a diff will be computed on a background thread, which
     * will dispatch Adapter.notifyItem events on the main thread.
     *
     * @param list The new list to be displayed.
     */
    fun submitList(list: List<T>) {
        mDiffer.submitList(list)
    }

    /**
     * Set the new list to be displayed.
     *
     *
     * If a List is already being displayed, a diff will be computed on a background thread, which
     * will dispatch Adapter.notifyItem events on the main thread.
     *
     *
     * The commit callback can be used to know when the List is committed, but note that it
     * may not be executed. If List B is submitted immediately after List A, and is
     * committed directly, the callback associated with List A will not be run.
     *
     * @param list The new list to be displayed.
     * @param commitCallback Optional runnable that is executed when the List is committed, if
     * it is committed.
     */
    fun submitList(list: List<T>, commitCallback: Runnable?) {
        mDiffer.submitList(list, commitCallback)
    }

    protected fun getItem(position: Int): T {
        return mDiffer.getCurrentList().get(position)
    }

    override fun getItemCount(): Int {
        return mDiffer.getCurrentList().size
    }

    override fun onBindAnnotation(annotation: MapAnnotation, position: Int, payload: Any?) {
        onBindAnnotation(annotation,position,payload)
    }

    override fun onCreateAnnotation(
        factory: AnnotationFactory<GoogleMap>,
        position: Int,
        annotationType: Int
    ): MapAnnotation {
        return onCreateAnnotation(factory, position, annotationType)
    }

    /**
     * Get the current List - any diffing to present this list has already been computed and
     * dispatched via the ListUpdateCallback.
     *
     *
     * If a `null` List, or no List has been submitted, an empty list will be returned.
     *
     *
     * The returned list may not be mutated - mutations to content must be done through
     * [.submitList].
     *
     * @return The list currently being displayed.
     *
     * @see .onCurrentListChanged
     */
    fun getCurrentList(): List<T> {
        return mDiffer.getCurrentList()
    }

    /**
     * Called when the current List is updated.
     *
     *
     * If a `null` List is passed to [.submitList], or no List has been
     * submitted, the current List is represented as an empty List.
     *
     * @param previousList List that was displayed previously.
     * @param currentList new List being displayed, will be empty if `null` was passed to
     * [.submitList].
     *
     * @see .getCurrentList
     */
    fun onCurrentListChanged(previousList: List<T>, currentList: List<T>) {}
}


/**
 * ListUpdateCallback that dispatches update events to the given adapter.
 *
 * @see DiffUtil.DiffResult.dispatchUpdatesTo
 */
class AdapterListUpdateCallbackCustom
/**
 * Creates an AdapterListUpdateCallback that will dispatch update events to the given adapter.
 *
 * @param adapter The Adapter to send updates to.
 */(private val mAdapter: GoogleMapMeAdapter) :
    ListUpdateCallback {

    /** {@inheritDoc}  */
    override fun onInserted(position: Int, count: Int) {
        mAdapter.notifyItemRangeInserted(position, count)
    }

    /** {@inheritDoc}  */
    override fun onRemoved(position: Int, count: Int) {
        mAdapter.notifyItemRangeRemoved(position, count)
    }

    /** {@inheritDoc}  */
    override fun onMoved(fromPosition: Int, toPosition: Int) {
        mAdapter.notifyItemMoved(fromPosition, toPosition)
    }

    /** {@inheritDoc}  */
    override fun onChanged(position: Int, count: Int, payload: Any?) {
        mAdapter.notifyItemRangeChanged(position, count, payload)
    }
}



//class Test : RecyclerView<>() {
//
//
//
//}