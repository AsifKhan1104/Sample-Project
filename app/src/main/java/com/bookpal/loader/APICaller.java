package com.bookpal.loader;


import com.bookpal.model.Model;

/**
 * @author asif
 *         <p/>
 *         Interface for async executor to get callback response
 *         object or collection of Model object
 */
public interface APICaller {
    public void onComplete(Model model);
}
