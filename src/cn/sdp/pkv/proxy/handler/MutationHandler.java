package cn.sdp.pkv.proxy.handler;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

public class MutationHandler extends PKVHandler {

	protected final CompletionService<Integer> intServ;

	public MutationHandler(String tbName, ExecutorService service) {
		super(tbName);
		this.intServ = new ExecutorCompletionService<Integer>(service);
	}

}