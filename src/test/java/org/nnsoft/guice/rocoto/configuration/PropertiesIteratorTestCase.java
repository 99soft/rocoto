package org.nnsoft.guice.rocoto.configuration;

import static org.junit.Assert.assertFalse;
import static org.nnsoft.guice.rocoto.configuration.PropertiesIterator.newPropertiesIterator;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

/**
 * Test case on {@link PropertiesIterator}.
 */
public final class PropertiesIteratorTestCase
{

	/**
	 * Test thread safety of properties iterator.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void verifyThreadSafety() throws InterruptedException
	{
		ExecutorService service = Executors.newFixedThreadPool(2);

		// Callable iterating over System.getProperties()
		Future<Void> future = service.submit(new Callable<Void>()
		{

			public Void call() throws Exception
			{
				try
				{
					while (true)
					{
						Iterator<Entry<String, String>> it = newPropertiesIterator(System.getProperties());
						while (it.hasNext())
						{
							it.next();
						}
						Thread.sleep(10L);
					}
				} catch (InterruptedException e)
				{
					return null;
				}
			}
		});
		// Runnable updating System.getProperties()
		service.execute(new Runnable()
		{
			public void run()
			{
				while (true)
				{
					String key = "new key " + System.nanoTime();
					System.getProperties().setProperty(key, "test");
					try
					{
						Thread.sleep(10L);
					} catch (InterruptedException e)
					{
						return;
					}
					System.getProperties().remove(key);
				}
			}
		});

		// Process for 2 seconds
		Thread.sleep(2000L);
		service.shutdownNow();
		try
		{
			future.get();
		} catch (ExecutionException e)
		{
			// If cause of execution failure is ConcurrentModificationException, iterator is not thread safe
			assertFalse(e.getCause() instanceof ConcurrentModificationException);
		}
	}
}
