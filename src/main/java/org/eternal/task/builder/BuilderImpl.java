package org.eternal.task.builder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.eternal.eventbus.EventResult;
import org.eternal.task.TaskEvent;
import org.eternal.task.builder.Builder.FlowBuilder;
import org.eternal.task.builder.Builder.TaskBuilder;
import org.eternal.task.builder.Meta.Ware;
import org.eternal.task.handler.TaskHandler;
import org.eternal.task.handler.TaskService;
import org.eternal.task.router.Router;

/***
 * 
 * @author gaoqi
 *
 */
class BuilderImpl {

    BuilderImpl() {

    }

    static class TaskBuilderImpl implements TaskBuilder, Builder {

        Map<String, Meta> units = new ConcurrentHashMap<>();

        final String code;

        protected TaskBuilderImpl(String code) {
            this.code = code;
        }

        @Override
        public TaskBuilder match(Class<? extends TaskEvent> type, FlowBuilder flow) {
            return matchFlow(type.getName(), flow);
        }

        @Override
        public TaskBuilder matchFlow(String value, FlowBuilder flow) {
            units.put(value, Meta.flower(value, flow));
            return this;
        }

        @Override
        public <T extends TaskHandler> TaskBuilder match(Class<? extends TaskEvent> type, T handler) {
            return matchEquals(type.getName(), handler);
        }

        @Override
        public TaskBuilder matchEquals(String value, TaskHandler handler) {
            units.put(value, Meta.handler(value, handler));
            return this;
        }

        @Override
        public TaskBuilder match(Class<? extends TaskEvent> type, Function<TaskEvent, EventResult> func) {
            return matchEquals(type.getName(), func);
        }

        @Override
        public TaskBuilder matchEquals(String value, Function<TaskEvent, EventResult> func) {
            units.put(value, Meta.handler(value, new TaskHandler() {
                TaskService defService = new TaskService() {
                    @Override
                    public EventResult accept(TaskEvent event) {
                        return func.apply(event);
                    }
                };

                @SuppressWarnings("unchecked")
                @Override
                protected <V, S extends TaskService> S getTaskService(Optional<V> args) {
                    return (S) defService;
                }
            }));

            return this;
        }

        @Override
        public Router build() throws Exception {
            return Router.singleThen(code, router -> {
                return new _Ware(router).build(this).routes;
            });
        }

        @Override
        public Builder gotta() {
            return this;
        }

        @Override
        public String getName() {
            return code;
        }

        // ---------------------------------------------
        private class _Ware extends Meta.Ware {

            _Ware(final Router r) {
                super(r);
            }

            @Override
            protected <T> Ware build(T args) {
                TaskBuilderImpl tb = (TaskBuilderImpl) args;
                for (String key : tb.units.keySet()) {
                    Meta u = tb.units.get(key);
                    if (u instanceof Meta.Meta4Flow) {
                        this.addRoute((Meta.Meta4Flow) u);
                    } else {
                        hasChildren(u);
                    }
                }
                return this;
            }

            void hasChildren(Meta unit) {

                register(unit);

                TaskBuilderImpl impl = (TaskBuilderImpl) unit.getExtObject();
                if (impl == null) {
                    addNull(unit);
                    return;
                }

                if (impl.units.isEmpty()) {
                    addNull(unit);
                } else {
                    addRoute(unit, impl.units);
                }
            }

            void addNull(Meta source) {
                this.routes.put(source.getKey(), Router.Null);
            }

            void addRoute(Meta.Meta4Flow meta) {
                try {
                    this.routes.put(meta.getKey(), ((Builder) meta.getObject()).build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            void addRoute(Meta source, Meta target) {
                this.routes.put(source.getKey(), r.getPath(target.getKey()));
            }

            void addRoute(Meta source, Map<String, Meta> units) {
                int n = 0;
                for (String key : units.keySet()) {
                    Meta u = units.get(key);
                    if (n == 0) { // first one line add ;
                        addRoute(source, u);
                    }
                    hasChildren(u);
                    n++;
                }
            }

            void register(Meta unit) {
                this.eb.register(unit.getKey(), unit.getObject());
                this.eb.register(r.getPath(unit.getKey()), unit.getObject());
            }
        }
    }

    // ------------------
    static class FlowBuilderImpl implements FlowBuilder, Builder {

        final String name;

        Meta root;

        Meta last;

        protected FlowBuilderImpl(String name) {
            this.name = name;
        }

        @Override
        public FlowBuilder next(String name, TaskHandler handler) {
            Meta node = Meta.handler(name, handler);
            last = Optional.ofNullable(root).map(root -> {
                last.withExtObject(node);
                return node;
            }).orElseGet(() -> {
                root = node;
                return node;
            });
            return this;
        }

        @Override
        public Router build() throws Exception {
            if (root == null) {
                throw new Exception("root is null");
            }
            return Router.lineThen(name, router -> {
                return new _Ware(router).build(this).routes;
            });
        }

        @Override
        public String getName() {
            return name;
        }

        private class _Ware extends Meta.Ware {

            _Ware(Router r) {
                super(r);
            }

            void addRoute(Meta node) {

                this.routes.put(node.getKey(),
                        node.getExtObject() == null ? Router.Null : r.getPath(((Meta) node.getExtObject()).getKey()));
            }

            void register(Meta node) {
                this.eb.register(r.getPath(node.getKey()), node.getObject());
            }

            @Override
            protected <T> Ware build(T args) {

                Meta root = ((FlowBuilderImpl) args).root;

                routes.put(r.getName(), r.getPath(root.getKey()));

                Meta current = root;

                while (current != null) {
                    register(current);
                    addRoute(current);
                    current = current.getExtObject();
                }

                return this;
            }

        }

        @Override
        public Builder gotta() {
            return this;
        }

    }

}
