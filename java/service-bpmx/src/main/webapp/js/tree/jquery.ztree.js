/*
 * JQuery zTree core 3.5.14
 * http://zTree.me/
 *
 * Copyright (c) 2010 Hunter.z
 *
 * Licensed same as jquery - MIT License
 * http://www.opensource.org/licenses/mit-license.php
 *
 * email: hunter.z@263.net
 * Date: 2013-06-28
 */
(function(p) {
	var G, H, I, J, K, L, r = {},
		u = {},
		v = {},
		M = {
			treeId: "",
			treeObj: null,
			view: {
				addDiyDom: null,
				autoCancelSelected: !0,
				dblClickExpand: !0,
				expandSpeed: "fast",
				fontCss: {},
				nameIsHTML: !1,
				selectedMulti: !0,
				showIcon: !0,
				showLine: !0,
				showTitle: !0
			},
			data: {
				key: {
					children: "children",
					name: "name",
					title: "",
					url: "url"
				},
				simpleData: {
					enable: !1,
					idKey: "id",
					pIdKey: "pId",
					rootPId: null
				},
				keep: {
					parent: !1,
					leaf: !1
				}
			},
			async: {
				enable: !1,
				contentType: "application/x-www-form-urlencoded",
				type: "post",
				dataType: "text",
				url: "",
				autoParam: [],
				otherParam: [],
				dataFilter: null
			},
			callback: {
				beforeAsync: null,
				beforeClick: null,
				beforeDblClick: null,
				beforeRightClick: null,
				beforeMouseDown: null,
				beforeMouseUp: null,
				beforeExpand: null,
				beforeCollapse: null,
				beforeRemove: null,
				onAsyncError: null,
				onAsyncSuccess: null,
				onNodeCreated: null,
				onClick: null,
				onDblClick: null,
				onRightClick: null,
				onMouseDown: null,
				onMouseUp: null,
				onExpand: null,
				onCollapse: null,
				onRemove: null
			}
		},
		w = [function(b) {
			var a = b.treeObj,
				c = e.event;
			a.bind(c.NODECREATED, function(a, c, g) {
				j.apply(b.callback.onNodeCreated, [a, c, g])
			});
			a.bind(c.CLICK, function(a, c, g, l, h) {
				j.apply(b.callback.onClick, [c, g, l, h])
			});
			a.bind(c.EXPAND, function(a, c, g) {
				j.apply(b.callback.onExpand, [a, c, g])
			});
			a.bind(c.COLLAPSE, function(a, c, g) {
				j.apply(b.callback.onCollapse, [a, c, g])
			});
			a.bind(c.ASYNC_SUCCESS, function(a, c, g, l) {
				j.apply(b.callback.onAsyncSuccess, [a, c, g, l])
			});
			a.bind(c.ASYNC_ERROR, function(a, c, g, l, h, e) {
				j.apply(b.callback.onAsyncError, [a, c, g, l, h, e])
			})
		}],
		x = [function(b) {
			var a = e.event;
			b.treeObj.unbind(a.NODECREATED).unbind(a.CLICK).unbind(a.EXPAND).unbind(a.COLLAPSE).unbind(a.ASYNC_SUCCESS).unbind(a.ASYNC_ERROR)
		}],
		y = [function(b) {
			var a = h.getCache(b);
			a || (a = {}, h.setCache(b, a));
			a.nodes = [];
			a.doms = []
		}],
		z = [function(b, a, c, d, f, g) {
			if (c) {
				var l = h.getRoot(b),
					e = b.data.key.children;
				c.level = a;
				c.tId = b.treeId + "_" + ++l.zId;
				c.parentTId = d ? d.tId : null;
				if (c[e] && c[e].length > 0) {
					if (typeof c.open == "string") c.open = j.eqs(c.open, "true");
					c.open = !! c.open;
					c.isParent = !0;
					c.zAsync = !0
				} else {
					c.open = !1;
					if (typeof c.isParent == "string") c.isParent = j.eqs(c.isParent, "true");
					c.isParent = !! c.isParent;
					c.zAsync = !c.isParent
				}
				c.isFirstNode = f;
				c.isLastNode = g;
				c.getParentNode = function() {
					return h.getNodeCache(b, c.parentTId)
				};
				c.getPreNode = function() {
					return h.getPreNode(b, c)
				};
				c.getNextNode = function() {
					return h.getNextNode(b, c)
				};
				c.isAjaxing = !1;
				h.fixPIdKeyValue(b, c)
			}
		}],
		s = [function(b) {
			var a = b.target,
				c = h.getSetting(b.data.treeId),
				d = "",
				f = null,
				g = "",
				l = "",
				i = null,
				n = null,
				k = null;
			if (j.eqs(b.type, "mousedown")) l = "mousedown";
			else if (j.eqs(b.type, "mouseup")) l = "mouseup";
			else if (j.eqs(b.type, "contextmenu")) l = "contextmenu";
			else if (j.eqs(b.type, "click")) if (j.eqs(a.tagName, "span") && a.getAttribute("treeNode" + e.id.SWITCH) !== null) d = j.getNodeMainDom(a).id, g = "switchNode";
			else {
				if (k = j.getMDom(c, a, [{
					tagName: "a",
					attrName: "treeNode" + e.id.A
				}])) d = j.getNodeMainDom(k).id, g = "clickNode"
			} else if (j.eqs(b.type, "dblclick") && (l = "dblclick", k = j.getMDom(c, a, [{
				tagName: "a",
				attrName: "treeNode" + e.id.A
			}]))) d = j.getNodeMainDom(k).id, g = "switchNode";
			if (l.length > 0 && d.length == 0 && (k = j.getMDom(c, a, [{
				tagName: "a",
				attrName: "treeNode" + e.id.A
			}]))) d = j.getNodeMainDom(k).id;
			if (d.length > 0) switch (f = h.getNodeCache(c, d), g) {
			case "switchNode":
				f.isParent ? j.eqs(b.type, "click") || j.eqs(b.type, "dblclick") && j.apply(c.view.dblClickExpand, [c.treeId, f], c.view.dblClickExpand) ? i = G : g = "" : g = "";
				break;
			case "clickNode":
				i = H
			}
			switch (l) {
			case "mousedown":
				n = I;
				break;
			case "mouseup":
				n = J;
				break;
			case "dblclick":
				n = K;
				break;
			case "contextmenu":
				n = L
			}
			return {
				stop: !1,
				node: f,
				nodeEventType: g,
				nodeEventCallback: i,
				treeEventType: l,
				treeEventCallback: n
			}
		}],
		A = [function(b) {
			var a = h.getRoot(b);
			a || (a = {}, h.setRoot(b, a));
			a[b.data.key.children] = [];
			a.expandTriggerFlag = !1;
			a.curSelectedList = [];
			a.noSelection = !0;
			a.createdNodes = [];
			a.zId = 0;
			a._ver = (new Date).getTime()
		}],
		B = [],
		C = [],
		D = [],
		E = [],
		F = [],
		h = {
			addNodeCache: function(b, a) {
				h.getCache(b).nodes[h.getNodeCacheId(a.tId)] = a
			},
			getNodeCacheId: function(b) {
				return b.substring(b.lastIndexOf("_") + 1)
			},
			addAfterA: function(b) {
				C.push(b)
			},
			addBeforeA: function(b) {
				B.push(b)
			},
			addInnerAfterA: function(b) {
				E.push(b)
			},
			addInnerBeforeA: function(b) {
				D.push(b)
			},
			addInitBind: function(b) {
				w.push(b)
			},
			addInitUnBind: function(b) {
				x.push(b)
			},
			addInitCache: function(b) {
				y.push(b)
			},
			addInitNode: function(b) {
				z.push(b)
			},
			addInitProxy: function(b, a) {
				a ? s.splice(0, 0, b) : s.push(b)
			},
			addInitRoot: function(b) {
				A.push(b)
			},
			addNodesData: function(b, a, c) {
				var d = b.data.key.children;
				a[d] || (a[d] = []);
				if (a[d].length > 0) a[d][a[d].length - 1].isLastNode = !1, i.setNodeLineIcos(b, a[d][a[d].length - 1]);
				a.isParent = !0;
				a[d] = a[d].concat(c)
			},
			addSelectedNode: function(b, a) {
				var c = h.getRoot(b);
				h.isSelectedNode(b, a) || c.curSelectedList.push(a)
			},
			addCreatedNode: function(b, a) {
				(b.callback.onNodeCreated || b.view.addDiyDom) && h.getRoot(b).createdNodes.push(a)
			},
			addZTreeTools: function(b) {
				F.push(b)
			},
			exSetting: function(b) {
				p.extend(!0, M, b)
			},
			fixPIdKeyValue: function(b, a) {
				b.data.simpleData.enable && (a[b.data.simpleData.pIdKey] = a.parentTId ? a.getParentNode()[b.data.simpleData.idKey] : b.data.simpleData.rootPId)
			},
			getAfterA: function(b, a, c) {
				for (var d = 0, f = C.length; d < f; d++) C[d].apply(this, arguments)
			},
			getBeforeA: function(b, a, c) {
				for (var d = 0, f = B.length; d < f; d++) B[d].apply(this, arguments)
			},
			getInnerAfterA: function(b, a, c) {
				for (var d = 0, f = E.length; d < f; d++) E[d].apply(this, arguments)
			},
			getInnerBeforeA: function(b, a, c) {
				for (var d = 0, f = D.length; d < f; d++) D[d].apply(this, arguments)
			},
			getCache: function(b) {
				return v[b.treeId]
			},
			getNextNode: function(b, a) {
				if (!a) return null;
				for (var c = b.data.key.children, d = a.parentTId ? a.getParentNode() : h.getRoot(b), f = 0, g = d[c].length - 1; f <= g; f++) if (d[c][f] === a) return f == g ? null : d[c][f + 1];
				return null
			},
			getNodeByParam: function(b, a, c, d) {
				if (!a || !c) return null;
				for (var f = b.data.key.children, g = 0, l = a.length; g < l; g++) {
					if (a[g][c] == d) return a[g];
					var e = h.getNodeByParam(b, a[g][f], c, d);
					if (e) return e
				}
				return null
			},
			getNodeCache: function(b, a) {
				if (!a) return null;
				var c = v[b.treeId].nodes[h.getNodeCacheId(a)];
				return c ? c : null
			},
			getNodeName: function(b, a) {
				return "" + a[b.data.key.name]
			},
			getNodeTitle: function(b, a) {
				return "" + a[b.data.key.title === "" ? b.data.key.name : b.data.key.title]
			},
			getNodes: function(b) {
				return h.getRoot(b)[b.data.key.children]
			},
			getNodesByParam: function(b, a, c, d) {
				if (!a || !c) return [];
				for (var f = b.data.key.children, g = [], l = 0, e = a.length; l < e; l++) a[l][c] == d && g.push(a[l]), g = g.concat(h.getNodesByParam(b, a[l][f], c, d));
				return g
			},
			getNodesByParamFuzzy: function(b, a, c, d) {
				if (!a || !c) return [];
				for (var f = b.data.key.children, g = [], d = d.toLowerCase(), l = 0, e = a.length; l < e; l++) typeof a[l][c] == "string" && a[l][c].toLowerCase().indexOf(d) > -1 && g.push(a[l]), g = g.concat(h.getNodesByParamFuzzy(b, a[l][f], c, d));
				return g
			},
			getNodesByFilter: function(b, a, c, d, f) {
				if (!a) return d ? null : [];
				for (var g = b.data.key.children, e = d ? null : [], i = 0, n = a.length; i < n; i++) {
					if (j.apply(c, [a[i], f], !1)) {
						if (d) return a[i];
						e.push(a[i])
					}
					var k = h.getNodesByFilter(b, a[i][g], c, d, f);
					if (d && k) return k;
					e = d ? k : e.concat(k)
				}
				return e
			},
			getPreNode: function(b, a) {
				if (!a) return null;
				for (var c = b.data.key.children, d = a.parentTId ? a.getParentNode() : h.getRoot(b), f = 0, g = d[c].length; f < g; f++) if (d[c][f] === a) return f == 0 ? null : d[c][f - 1];
				return null
			},
			getRoot: function(b) {
				return b ? u[b.treeId] : null
			},
			getRoots: function() {
				return u
			},
			getSetting: function(b) {
				return r[b]
			},
			getSettings: function() {
				return r
			},
			getZTreeTools: function(b) {
				return (b = this.getRoot(this.getSetting(b))) ? b.treeTools : null
			},
			initCache: function(b) {
				for (var a = 0, c = y.length; a < c; a++) y[a].apply(this, arguments)
			},
			initNode: function(b, a, c, d, f, g) {
				for (var e = 0, h = z.length; e < h; e++) z[e].apply(this, arguments)
			},
			initRoot: function(b) {
				for (var a = 0, c = A.length; a < c; a++) A[a].apply(this, arguments)
			},
			isSelectedNode: function(b, a) {
				for (var c = h.getRoot(b), d = 0, f = c.curSelectedList.length; d < f; d++) if (a === c.curSelectedList[d]) return !0;
				return !1
			},
			removeNodeCache: function(b, a) {
				var c = b.data.key.children;
				if (a[c]) for (var d = 0, f = a[c].length; d < f; d++) arguments.callee(b, a[c][d]);
				h.getCache(b).nodes[h.getNodeCacheId(a.tId)] = null
			},
			removeSelectedNode: function(b, a) {
				for (var c = h.getRoot(b), d = 0, f = c.curSelectedList.length; d < f; d++) if (a === c.curSelectedList[d] || !h.getNodeCache(b, c.curSelectedList[d].tId)) c.curSelectedList.splice(d, 1), d--, f--
			},
			setCache: function(b, a) {
				v[b.treeId] = a
			},
			setRoot: function(b, a) {
				u[b.treeId] = a
			},
			setZTreeTools: function(b, a) {
				for (var c = 0, d = F.length; c < d; c++) F[c].apply(this, arguments)
			},
			transformToArrayFormat: function(b, a) {
				if (!a) return [];
				var c = b.data.key.children,
					d = [];
				if (j.isArray(a)) for (var f = 0, g = a.length; f < g; f++) d.push(a[f]), a[f][c] && (d = d.concat(h.transformToArrayFormat(b, a[f][c])));
				else d.push(a), a[c] && (d = d.concat(h.transformToArrayFormat(b, a[c])));
				return d
			},
			transformTozTreeFormat: function(b, a) {
				var c, d, f = b.data.simpleData.idKey,
					g = b.data.simpleData.pIdKey,
					e = b.data.key.children;
				if (!f || f == "" || !a) return [];
				if (j.isArray(a)) {
					var h = [],
						i = [];
					for (c = 0, d = a.length; c < d; c++) i[a[c][f]] = a[c];
					for (c = 0, d = a.length; c < d; c++) i[a[c][g]] && a[c][f] != a[c][g] ? (i[a[c][g]][e] || (i[a[c][g]][e] = []), i[a[c][g]][e].push(a[c])) : h.push(a[c]);
					return h
				} else return [a]
			}
		},
		m = {
			bindEvent: function(b) {
				for (var a = 0, c = w.length; a < c; a++) w[a].apply(this, arguments)
			},
			unbindEvent: function(b) {
				for (var a = 0, c = x.length; a < c; a++) x[a].apply(this, arguments)
			},
			bindTree: function(b) {
				var a = {
					treeId: b.treeId
				},
					b = b.treeObj;
				b.bind("selectstart", function(a) {
					a = a.originalEvent.srcElement.nodeName.toLowerCase();
					return a === "input" || a === "textarea"
				}).css({
					"-moz-user-select": "-moz-none"
				});
				b.bind("click", a, m.proxy);
				b.bind("dblclick", a, m.proxy);
				b.bind("mouseover", a, m.proxy);
				b.bind("mouseout", a, m.proxy);
				b.bind("mousedown", a, m.proxy);
				b.bind("mouseup", a, m.proxy);
				b.bind("contextmenu", a, m.proxy)
			},
			unbindTree: function(b) {
				b.treeObj.unbind("click", m.proxy).unbind("dblclick", m.proxy).unbind("mouseover", m.proxy).unbind("mouseout", m.proxy).unbind("mousedown", m.proxy).unbind("mouseup", m.proxy).unbind("contextmenu", m.proxy)
			},
			doProxy: function(b) {
				for (var a = [], c = 0, d = s.length; c < d; c++) {
					var f = s[c].apply(this, arguments);
					a.push(f);
					if (f.stop) break
				}
				return a
			},
			proxy: function(b) {
				var a = h.getSetting(b.data.treeId);
				if (!j.uCanDo(a, b)) return !0;
				for (var a = m.doProxy(b), c = !0, d = 0, f = a.length; d < f; d++) {
					var g = a[d];
					g.nodeEventCallback && (c = g.nodeEventCallback.apply(g, [b, g.node]) && c);
					g.treeEventCallback && (c = g.treeEventCallback.apply(g, [b, g.node]) && c)
				}
				return c
			}
		};
	G = function(b, a) {
		var c = h.getSetting(b.data.treeId);
		if (a.open) {
			if (j.apply(c.callback.beforeCollapse, [c.treeId, a], !0) == !1) return !0
		} else if (j.apply(c.callback.beforeExpand, [c.treeId, a], !0) == !1) return !0;
		h.getRoot(c).expandTriggerFlag = !0;
		i.switchNode(c, a);
		return !0
	};
	H = function(b, a) {
		var c = h.getSetting(b.data.treeId),
			d = c.view.autoCancelSelected && b.ctrlKey && h.isSelectedNode(c, a) ? 0 : c.view.autoCancelSelected && b.ctrlKey && c.view.selectedMulti ? 2 : 1;
		if (j.apply(c.callback.beforeClick, [c.treeId, a, d], !0) == !1) return !0;
		d === 0 ? i.cancelPreSelectedNode(c, a) : i.selectNode(c, a, d === 2);
		c.treeObj.trigger(e.event.CLICK, [b, c.treeId, a, d]);
		return !0
	};
	I = function(b, a) {
		var c = h.getSetting(b.data.treeId);
		j.apply(c.callback.beforeMouseDown, [c.treeId, a], !0) && j.apply(c.callback.onMouseDown, [b, c.treeId, a]);
		return !0
	};
	J = function(b, a) {
		var c = h.getSetting(b.data.treeId);
		j.apply(c.callback.beforeMouseUp, [c.treeId, a], !0) && j.apply(c.callback.onMouseUp, [b, c.treeId, a]);
		return !0
	};
	K = function(b, a) {
		var c = h.getSetting(b.data.treeId);
		j.apply(c.callback.beforeDblClick, [c.treeId, a], !0) && j.apply(c.callback.onDblClick, [b, c.treeId, a]);
		return !0
	};
	L = function(b, a) {
		var c = h.getSetting(b.data.treeId);
		j.apply(c.callback.beforeRightClick, [c.treeId, a], !0) && j.apply(c.callback.onRightClick, [b, c.treeId, a]);
		return typeof c.callback.onRightClick != "function"
	};
	var j = {
		apply: function(b, a, c) {
			return typeof b == "function" ? b.apply(N, a ? a : []) : c
		},
		canAsync: function(b, a) {
			var c = b.data.key.children;
			return b.async.enable && a && a.isParent && !(a.zAsync || a[c] && a[c].length > 0)
		},
		clone: function(b) {
			if (b === null) return null;
			var a = j.isArray(b) ? [] : {},
				c;
			for (c in b) a[c] = b[c] instanceof Date ? new Date(b[c].getTime()) : typeof b[c] === "object" ? arguments.callee(b[c]) : b[c];
			return a
		},
		eqs: function(b, a) {
			return b.toLowerCase() === a.toLowerCase()
		},
		isArray: function(b) {
			return Object.prototype.toString.apply(b) === "[object Array]"
		},
		$: function(b, a, c) {
			a && typeof a != "string" && (c = a, a = "");
			return typeof b == "string" ? p(b, c ? c.treeObj.get(0).ownerDocument : null) : p("#" + b.tId + a, c ? c.treeObj : null)
		},
		getMDom: function(b, a, c) {
			if (!a) return null;
			for (; a && a.id !== b.treeId;) {
				for (var d = 0, f = c.length; a.tagName && d < f; d++) if (j.eqs(a.tagName, c[d].tagName) && a.getAttribute(c[d].attrName) !== null) return a;
				a = a.parentNode
			}
			return null
		},
		getNodeMainDom: function(b) {
			return p(b).parent("li").get(0) || p(b).parentsUntil("li").parent().get(0)
		},
		uCanDo: function() {
			return !0
		}
	},
		i = {
			addNodes: function(b, a, c, d) {
				if (!b.data.keep.leaf || !a || a.isParent) if (j.isArray(c) || (c = [c]), b.data.simpleData.enable && (c = h.transformTozTreeFormat(b, c)), a) {
					var f = k(a, e.id.SWITCH, b),
						g = k(a, e.id.ICON, b),
						l = k(a, e.id.UL, b);
					if (!a.open) i.replaceSwitchClass(a, f, e.folder.CLOSE), i.replaceIcoClass(a, g, e.folder.CLOSE), a.open = !1, l.css({
						display: "none"
					});
					h.addNodesData(b, a, c);
					i.createNodes(b, a.level + 1, c, a);
					d || i.expandCollapseParentNode(b, a, !0)
				} else h.addNodesData(b, h.getRoot(b), c), i.createNodes(b, 0, c, null)
			},
			appendNodes: function(b, a, c, d, f, g) {
				if (!c) return [];
				for (var e = [], j = b.data.key.children, k = 0, m = c.length; k < m; k++) {
					var o = c[k];
					if (f) {
						var t = (d ? d : h.getRoot(b))[j].length == c.length && k == 0;
						h.initNode(b, a, o, d, t, k == c.length - 1, g);
						h.addNodeCache(b, o)
					}
					t = [];
					o[j] && o[j].length > 0 && (t = i.appendNodes(b, a + 1, o[j], o, f, g && o.open));
					g && (i.makeDOMNodeMainBefore(e, b, o), i.makeDOMNodeLine(e, b, o), h.getBeforeA(b, o, e), i.makeDOMNodeNameBefore(e, b, o), h.getInnerBeforeA(b, o, e), i.makeDOMNodeIcon(e, b, o), h.getInnerAfterA(b, o, e), i.makeDOMNodeNameAfter(e, b, o), h.getAfterA(b, o, e), o.isParent && o.open && i.makeUlHtml(b, o, e, t.join("")), i.makeDOMNodeMainAfter(e, b, o), h.addCreatedNode(b, o))
				}
				return e
			},
			appendParentULDom: function(b, a) {
				var c = [],
					d = k(a, b);
				!d.get(0) && a.parentTId && (i.appendParentULDom(b, a.getParentNode()), d = k(a, b));
				var f = k(a, e.id.UL, b);
				f.get(0) && f.remove();
				f = i.appendNodes(b, a.level + 1, a[b.data.key.children], a, !1, !0);
				i.makeUlHtml(b, a, c, f.join(""));
				d.append(c.join(""))
			},
			asyncNode: function(b, a, c, d) {
				var f, g;
				if (a && !a.isParent) return j.apply(d), !1;
				else if (a && a.isAjaxing) return !1;
				else if (j.apply(b.callback.beforeAsync, [b.treeId, a], !0) == !1) return j.apply(d), !1;
				if (a) a.isAjaxing = !0, k(a, e.id.ICON, b).attr({
					style: "",
					"class": e.className.BUTTON + " " + e.className.ICO_LOADING
				});
				var l = {};
				for (f = 0, g = b.async.autoParam.length; a && f < g; f++) {
					var q = b.async.autoParam[f].split("="),
						n = q;
					q.length > 1 && (n = q[1], q = q[0]);
					l[n] = a[q]
				}
				if (j.isArray(b.async.otherParam)) for (f = 0, g = b.async.otherParam.length; f < g; f += 2) l[b.async.otherParam[f]] = b.async.otherParam[f + 1];
				else for (var m in b.async.otherParam) l[m] = b.async.otherParam[m];
				var o = h.getRoot(b)._ver;
				p.ajax({
					contentType: b.async.contentType,
					type: b.async.type,
					url: j.apply(b.async.url, [b.treeId, a], b.async.url),
					data: l,
					dataType: b.async.dataType,
					success: function(f) {
						if (o == h.getRoot(b)._ver) {
							var g = [];
							try {
								g = !f || f.length == 0 ? [] : typeof f == "string" ? eval("(" + f + ")") : f
							} catch (l) {
								g = f
							}
							if (a) a.isAjaxing = null, a.zAsync = !0;
							i.setNodeLineIcos(b, a);
							g && g !== "" ? (g = j.apply(b.async.dataFilter, [b.treeId, a, g], g), i.addNodes(b, a, g ? j.clone(g) : [], !! c)) : i.addNodes(b, a, [], !! c);
							b.treeObj.trigger(e.event.ASYNC_SUCCESS, [b.treeId, a, f]);
							j.apply(d)
						}
					},
					error: function(c, d, f) {
						if (o == h.getRoot(b)._ver) {
							if (a) a.isAjaxing = null;
							i.setNodeLineIcos(b, a);
							b.treeObj.trigger(e.event.ASYNC_ERROR, [b.treeId, a, c, d, f])
						}
					}
				});
				return !0
			},
			cancelPreSelectedNode: function(b, a) {
				for (var c = h.getRoot(b).curSelectedList, d = c.length - 1; d >= 0; d--) if (!a || a === c[d]) if (k(c[d], e.id.A, b).removeClass(e.node.CURSELECTED), a) {
					h.removeSelectedNode(b, a);
					break
				}
				if (!a) h.getRoot(b).curSelectedList = []
			},
			createNodeCallback: function(b) {
				if (b.callback.onNodeCreated || b.view.addDiyDom) for (var a = h.getRoot(b); a.createdNodes.length > 0;) {
					var c = a.createdNodes.shift();
					j.apply(b.view.addDiyDom, [b.treeId, c]);
					b.callback.onNodeCreated && b.treeObj.trigger(e.event.NODECREATED, [b.treeId, c])
				}
			},
			createNodes: function(b, a, c, d) {
				if (c && c.length != 0) {
					var f = h.getRoot(b),
						g = b.data.key.children,
						g = !d || d.open || !! k(d[g][0], b).get(0);
					f.createdNodes = [];
					a = i.appendNodes(b, a, c, d, !0, g);
					d ? (d = k(d, e.id.UL, b), d.get(0) && d.append(a.join(""))) : b.treeObj.append(a.join(""));
					i.createNodeCallback(b)
				}
			},
			destroy: function(b) {
				b && (h.initCache(b), h.initRoot(b), m.unbindTree(b), m.unbindEvent(b), b.treeObj.empty())
			},
			expandCollapseNode: function(b, a, c, d, f) {
				var g = h.getRoot(b),
					l = b.data.key.children;
				if (a) {
					if (g.expandTriggerFlag) {
						var q = f,
							f = function() {
								q && q();
								a.open ? b.treeObj.trigger(e.event.EXPAND, [b.treeId, a]) : b.treeObj.trigger(e.event.COLLAPSE, [b.treeId, a])
							};
						g.expandTriggerFlag = !1
					}
					if (!a.open && a.isParent && (!k(a, e.id.UL, b).get(0) || a[l] && a[l].length > 0 && !k(a[l][0], b).get(0))) i.appendParentULDom(b, a), i.createNodeCallback(b);
					if (a.open == c) j.apply(f, []);
					else {
						var c = k(a, e.id.UL, b),
							g = k(a, e.id.SWITCH, b),
							n = k(a, e.id.ICON, b);
						a.isParent ? (a.open = !a.open, a.iconOpen && a.iconClose && n.attr("style", i.makeNodeIcoStyle(b, a)), a.open ? (i.replaceSwitchClass(a, g, e.folder.OPEN), i.replaceIcoClass(a, n, e.folder.OPEN), d == !1 || b.view.expandSpeed == "" ? (c.show(), j.apply(f, [])) : a[l] && a[l].length > 0 ? c.slideDown(b.view.expandSpeed, f) : (c.show(), j.apply(f, []))) : (i.replaceSwitchClass(a, g, e.folder.CLOSE), i.replaceIcoClass(a, n, e.folder.CLOSE), d == !1 || b.view.expandSpeed == "" || !(a[l] && a[l].length > 0) ? (c.hide(), j.apply(f, [])) : c.slideUp(b.view.expandSpeed, f))) : j.apply(f, [])
					}
				} else j.apply(f, [])
			},
			expandCollapseParentNode: function(b, a, c, d, f) {
				a && (a.parentTId ? (i.expandCollapseNode(b, a, c, d), a.parentTId && i.expandCollapseParentNode(b, a.getParentNode(), c, d, f)) : i.expandCollapseNode(b, a, c, d, f))
			},
			expandCollapseSonNode: function(b, a, c, d, f) {
				var g = h.getRoot(b),
					e = b.data.key.children,
					g = a ? a[e] : g[e],
					e = a ? !1 : d,
					j = h.getRoot(b).expandTriggerFlag;
				h.getRoot(b).expandTriggerFlag = !1;
				if (g) for (var k = 0, m = g.length; k < m; k++) g[k] && i.expandCollapseSonNode(b, g[k], c, e);
				h.getRoot(b).expandTriggerFlag = j;
				i.expandCollapseNode(b, a, c, d, f)
			},
			makeDOMNodeIcon: function(b, a, c) {
				var d = h.getNodeName(a, c),
					d = a.view.nameIsHTML ? d : d.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
				b.push("<span id='", c.tId, e.id.ICON, "' title='treeNode", e.id.ICON, "' style='", i.makeNodeIcoStyle(a, c), "'><i ' class='", i.makeNodeIcoClass(a, c), "'></i></span><span id='", c.tId, e.id.SPAN, "'>", d, "</span>")
			},
			makeDOMNodeLine: function(b, a, c) {
				b.push("<span id='", c.tId, e.id.SWITCH, "' title='' class='", i.makeNodeLineClass(a, c), "' treeNode", e.id.SWITCH, "></span>")
			},
			makeDOMNodeMainAfter: function(b) {
				b.push("</li>")
			},
			makeDOMNodeMainBefore: function(b, a, c) {
				b.push("<li id='", c.tId, "' class='", e.className.LEVEL, c.level, "' tabindex='0' hidefocus='true' treenode>")
			},
			makeDOMNodeNameAfter: function(b) {
				b.push("</a>")
			},
			makeDOMNodeNameBefore: function(b, a, c) {
				var d = h.getNodeTitle(a, c),
					f = i.makeNodeUrl(a, c),
					g = i.makeNodeFontCss(a, c),
					l = [],
					k;
				for (k in g) l.push(k, ":", g[k], ";");
				b.push("<a id='", c.tId, e.id.A, "' class='", e.className.LEVEL, c.level, "' treeNode", e.id.A, ' onclick="', c.click || "", '" ', f != null && f.length > 0 ? "href='" + f + "'" : "", " target='", i.makeNodeTarget(c), "' style='", l.join(""), "'");
				j.apply(a.view.showTitle, [a.treeId, c], a.view.showTitle) && d && b.push("title='", d.replace(/'/g, "&#39;").replace(/</g, "&lt;").replace(/>/g, "&gt;"), "'");
				b.push(">")
			},
			makeNodeFontCss: function(b, a) {
				var c = j.apply(b.view.fontCss, [b.treeId, a], b.view.fontCss);
				return c && typeof c != "function" ? c : {}
			},
			makeNodeIcoClass: function(b, a) {
				var c = [];
				if (!a.isAjaxing) {
					var d = a.isParent && a.iconOpen && a.iconClose ? a.open ? a.iconOpen : a.iconClose : a.icon;
					d && c.push(d);
					(b.view.showIcon == !1 || !j.apply(b.view.showIcon, [b.treeId, a], !0)) && c.push("width:0px;height:0px;")
				}
				
				var k = ["ico"];
				a.isAjaxing || (k[0] = (a.iconSkin ? a.iconSkin + "_" : "") + k[0], a.isParent ? k.push(a.open ? e.folder.OPEN : e.folder.CLOSE) : k.push(e.folder.DOCU));
				
				return e.className.BUTTON + " " + k.join("_") +" "+ c.join("")
			},
			makeNodeIcoStyle: function(b, a) {
				var c = [];
				if (!a.isAjaxing) {
					var d = a.isParent && a.iconOpen && a.iconClose ? a.open ? a.iconOpen : a.iconClose : a.icon;
					//console.log(d);
					if(typeof d=='string'){
					if(d.indexOf("iconfont") < 0 ){
						d && c.push("background:url(", d, ") 0 0 no-repeat;");
					(b.view.showIcon == !1 || !j.apply(b.view.showIcon, [b.treeId, a], !0)) && c.push("width:0px;height:0px;")} 	
				}}
				return c.join("")
			},
			makeNodeLineClass: function(b, a) {
				var c = [];
				b.view.showLine ? a.level == 0 && a.isFirstNode && a.isLastNode ? c.push(e.line.ROOT) : a.level == 0 && a.isFirstNode ? c.push(e.line.ROOTS) : a.isLastNode ? c.push(e.line.BOTTOM) : c.push(e.line.CENTER) : c.push(e.line.NOLINE);
				a.isParent ? c.push(a.open ? e.folder.OPEN : e.folder.CLOSE) : c.push(e.folder.DOCU);
				return i.makeNodeLineClassEx(a) + c.join("_")
			},
			makeNodeLineClassEx: function(b) {
				return e.className.BUTTON + " " + e.className.LEVEL + b.level + " " + e.className.SWITCH + " "
			},
			makeNodeTarget: function(b) {
				return b.target || "_blank"
			},
			makeNodeUrl: function(b, a) {
				var c = b.data.key.url;
				return a[c] ? a[c] : null
			},
			makeUlHtml: function(b, a, c, d) {
				c.push("<ul id='", a.tId, e.id.UL, "' class='", e.className.LEVEL, a.level, " ", i.makeUlLineClass(b, a), "' style='display:", a.open ? "block" : "none", "'>");
				c.push(d);
				c.push("</ul>")
			},
			makeUlLineClass: function(b, a) {
				return b.view.showLine && !a.isLastNode ? e.line.LINE : ""
			},
			removeChildNodes: function(b, a) {
				if (a) {
					var c = b.data.key.children,
						d = a[c];
					if (d) {
						for (var f = 0, g = d.length; f < g; f++) h.removeNodeCache(b, d[f]);
						h.removeSelectedNode(b);
						delete a[c];
						b.data.keep.parent ? k(a, e.id.UL, b).empty() : (a.isParent = !1, a.open = !1, c = k(a, e.id.SWITCH, b), d = k(a, e.id.ICON, b), i.replaceSwitchClass(a, c, e.folder.DOCU), i.replaceIcoClass(a, d, e.folder.DOCU), k(a, e.id.UL, b).remove())
					}
				}
			},
			setFirstNode: function(b, a) {
				var c = b.data.key.children;
				if (a[c].length > 0) a[c][0].isFirstNode = !0
			},
			setLastNode: function(b, a) {
				var c = b.data.key.children,
					d = a[c].length;
				if (d > 0) a[c][d - 1].isLastNode = !0
			},
			removeNode: function(b, a) {
				var c = h.getRoot(b),
					d = b.data.key.children,
					f = a.parentTId ? a.getParentNode() : c;
				a.isFirstNode = !1;
				a.isLastNode = !1;
				a.getPreNode = function() {
					return null
				};
				a.getNextNode = function() {
					return null
				};
				if (h.getNodeCache(b, a.tId)) {
					k(a, b).remove();
					h.removeNodeCache(b, a);
					h.removeSelectedNode(b, a);
					for (var g = 0, l = f[d].length; g < l; g++) if (f[d][g].tId == a.tId) {
						f[d].splice(g, 1);
						break
					}
					i.setFirstNode(b, f);
					i.setLastNode(b, f);
					var j, g = f[d].length;
					if (!b.data.keep.parent && g == 0) f.isParent = !1, f.open = !1, g = k(f, e.id.UL, b), l = k(f, e.id.SWITCH, b), j = k(f, e.id.ICON, b), i.replaceSwitchClass(f, l, e.folder.DOCU), i.replaceIcoClass(f, j, e.folder.DOCU), g.css("display", "none");
					else if (b.view.showLine && g > 0) {
						var n = f[d][g - 1],
							g = k(n, e.id.UL, b),
							l = k(n, e.id.SWITCH, b);
						j = k(n, e.id.ICON, b);
						f == c ? f[d].length == 1 ? i.replaceSwitchClass(n, l, e.line.ROOT) : (c = k(f[d][0], e.id.SWITCH, b), i.replaceSwitchClass(f[d][0], c, e.line.ROOTS), i.replaceSwitchClass(n, l, e.line.BOTTOM)) : i.replaceSwitchClass(n, l, e.line.BOTTOM);
						g.removeClass(e.line.LINE)
					}
				}
			},
			replaceIcoClass: function(b, a, c) {
				if (a && !b.isAjaxing && (b = a.attr("class"), b != void 0)) {
					b = b.split("_");
					switch (c) {
					case e.folder.OPEN:
					case e.folder.CLOSE:
					case e.folder.DOCU:
						b[b.length - 1] = c
					}
					a.attr("class", b.join("_"))
				}
			},
			replaceSwitchClass: function(b, a, c) {
				if (a) {
					var d = a.attr("class");
					if (d != void 0) {
						d = d.split("_");
						switch (c) {
						case e.line.ROOT:
						case e.line.ROOTS:
						case e.line.CENTER:
						case e.line.BOTTOM:
						case e.line.NOLINE:
							d[0] = i.makeNodeLineClassEx(b) + c;
							break;
						case e.folder.OPEN:
						case e.folder.CLOSE:
						case e.folder.DOCU:
							d[1] = c
						}
						a.attr("class", d.join("_"));
						c !== e.folder.DOCU ? a.removeAttr("disabled") : a.attr("disabled", "disabled")
					}
				}
			},
			selectNode: function(b, a, c) {
				c || i.cancelPreSelectedNode(b);
				k(a, e.id.A, b).addClass(e.node.CURSELECTED);
				h.addSelectedNode(b, a)
			},
			setNodeFontCss: function(b, a) {
				var c = k(a, e.id.A, b),
					d = i.makeNodeFontCss(b, a);
				d && c.css(d)
			},
			setNodeLineIcos: function(b, a) {
				if (a) {
					var c = k(a, e.id.SWITCH, b),
						d = k(a, e.id.UL, b),
						f = k(a, e.id.ICON, b),
						g = i.makeUlLineClass(b, a);
					g.length == 0 ? d.removeClass(e.line.LINE) : d.addClass(g);
					c.attr("class", i.makeNodeLineClass(b, a));
					a.isParent ? c.removeAttr("disabled") : c.attr("disabled", "disabled");
					f.removeAttr("style");
					f.attr("style", i.makeNodeIcoStyle(b, a));
					f.attr("class", i.makeNodeIcoClass(b, a))
				}
			},
			setNodeName: function(b, a) {
				var c = h.getNodeTitle(b, a),
					d = k(a, e.id.SPAN, b);
				d.empty();
				b.view.nameIsHTML ? d.html(h.getNodeName(b, a)) : d.text(h.getNodeName(b, a));
				j.apply(b.view.showTitle, [b.treeId, a], b.view.showTitle) && k(a, e.id.A, b).attr("title", !c ? "" : c)
			},
			setNodeTarget: function(b, a) {
				k(a, e.id.A, b).attr("target", i.makeNodeTarget(a))
			},
			setNodeUrl: function(b, a) {
				var c = k(a, e.id.A, b),
					d = i.makeNodeUrl(b, a);
				d == null || d.length == 0 ? c.removeAttr("href") : c.attr("href", d)
			},
			switchNode: function(b, a) {
				a.open || !j.canAsync(b, a) ? i.expandCollapseNode(b, a, !a.open) : b.async.enable ? i.asyncNode(b, a) || i.expandCollapseNode(b, a, !a.open) : a && i.expandCollapseNode(b, a, !a.open)
			}
		};
	p.fn.zTree = {
		consts: {
			className: {
				BUTTON: "button",
				LEVEL: "level",
				ICO_LOADING: "ico_loading",
				SWITCH: "switch"
			},
			event: {
				NODECREATED: "ztree_nodeCreated",
				CLICK: "ztree_click",
				EXPAND: "ztree_expand",
				COLLAPSE: "ztree_collapse",
				ASYNC_SUCCESS: "ztree_async_success",
				ASYNC_ERROR: "ztree_async_error"
			},
			id: {
				A: "_a",
				ICON: "_ico",
				SPAN: "_span",
				SWITCH: "_switch",
				UL: "_ul"
			},
			line: {
				ROOT: "root",
				ROOTS: "roots",
				CENTER: "center",
				BOTTOM: "bottom",
				NOLINE: "noline",
				LINE: "line"
			},
			folder: {
				OPEN: "open",
				CLOSE: "close",
				DOCU: "docu"
			},
			node: {
				CURSELECTED: "curSelectedNode"
			}
		},
		_z: {
			tools: j,
			view: i,
			event: m,
			data: h
		},
		getZTreeObj: function(b) {
			return (b = h.getZTreeTools(b)) ? b : null
		},
		destroy: function(b) {
			if (b && b.length > 0) i.destroy(h.getSetting(b));
			else for (var a in r) i.destroy(r[a])
		},
		init: function(b, a, c) {
			var d = j.clone(M);
			p.extend(!0, d, a);
			d.treeId = b.attr("id");
			d.treeObj = b;
			d.treeObj.empty();
			r[d.treeId] = d;
			if (typeof document.body.style.maxHeight === "undefined") d.view.expandSpeed = "";
			h.initRoot(d);
			b = h.getRoot(d);
			a = d.data.key.children;
			c = c ? j.clone(j.isArray(c) ? c : [c]) : [];
			b[a] = d.data.simpleData.enable ? h.transformTozTreeFormat(d, c) : c;
			h.initCache(d);
			m.unbindTree(d);
			m.bindTree(d);
			m.unbindEvent(d);
			m.bindEvent(d);
			c = {
				setting: d,
				addNodes: function(a, b, c) {
					function e() {
						i.addNodes(d, a, h, c == !0)
					}
					if (!b) return null;
					a || (a = null);
					if (a && !a.isParent && d.data.keep.leaf) return null;
					var h = j.clone(j.isArray(b) ? b : [b]);
					j.canAsync(d, a) ? i.asyncNode(d, a, c, e) : e();
					return h
				},
				cancelSelectedNode: function(a) {
					i.cancelPreSelectedNode(d, a)
				},
				destroy: function() {
					i.destroy(d)
				},
				expandAll: function(a) {
					a = !! a;
					i.expandCollapseSonNode(d, null, a, !0);
					return a
				},
				expandNode: function(a, b, c, e, n) {
					if (!a || !a.isParent) return null;
					b !== !0 && b !== !1 && (b = !a.open);
					if ((n = !! n) && b && j.apply(d.callback.beforeExpand, [d.treeId, a], !0) == !1) return null;
					else if (n && !b && j.apply(d.callback.beforeCollapse, [d.treeId, a], !0) == !1) return null;
					b && a.parentTId && i.expandCollapseParentNode(d, a.getParentNode(), b, !1);
					if (b === a.open && !c) return null;
					h.getRoot(d).expandTriggerFlag = n;
					if (!j.canAsync(d, a) && c) i.expandCollapseSonNode(d, a, b, !0, function() {
						if (e !== !1) try {
							k(a, d).focus().blur()
						} catch (b) {}
					});
					else if (a.open = !b, i.switchNode(this.setting, a), e !== !1) try {
						k(a, d).focus().blur()
					} catch (m) {}
					return b
				},
				getNodes: function() {
					return h.getNodes(d)
				},
				getNodeByParam: function(a, b, c) {
					return !a ? null : h.getNodeByParam(d, c ? c[d.data.key.children] : h.getNodes(d), a, b)
				},
				getNodeByTId: function(a) {
					return h.getNodeCache(d, a)
				},
				getNodesByParam: function(a, b, c) {
					return !a ? null : h.getNodesByParam(d, c ? c[d.data.key.children] : h.getNodes(d), a, b)
				},
				getNodesByParamFuzzy: function(a, b, c) {
					return !a ? null : h.getNodesByParamFuzzy(d, c ? c[d.data.key.children] : h.getNodes(d), a, b)
				},
				getNodesByFilter: function(a, b, c, e) {
					b = !! b;
					return !a || typeof a != "function" ? b ? null : [] : h.getNodesByFilter(d, c ? c[d.data.key.children] : h.getNodes(d), a, b, e)
				},
				getNodeIndex: function(a) {
					if (!a) return null;
					for (var b = d.data.key.children, c = a.parentTId ? a.getParentNode() : h.getRoot(d), e = 0, i = c[b].length; e < i; e++) if (c[b][e] == a) return e;
					return -1
				},
				getSelectedNodes: function() {
					for (var a = [], b = h.getRoot(d).curSelectedList, c = 0, e = b.length; c < e; c++) a.push(b[c]);
					return a
				},
				isSelectedNode: function(a) {
					return h.isSelectedNode(d, a)
				},
				reAsyncChildNodes: function(a, b, c) {
					if (this.setting.async.enable) {
						var j = !a;
						j && (a = h.getRoot(d));
						if (b == "refresh") {
							for (var b = this.setting.data.key.children, m = 0, p = a[b] ? a[b].length : 0; m < p; m++) h.removeNodeCache(d, a[b][m]);
							h.removeSelectedNode(d);
							a[b] = [];
							j ? this.setting.treeObj.empty() : k(a, e.id.UL, d).empty()
						}
						i.asyncNode(this.setting, j ? null : a, !! c)
					}
				},
				refresh: function() {
					this.setting.treeObj.empty();
					var a = h.getRoot(d),
						b = a[d.data.key.children];
					h.initRoot(d);
					a[d.data.key.children] = b;
					h.initCache(d);
					i.createNodes(d, 0, a[d.data.key.children])
				},
				removeChildNodes: function(a) {
					if (!a) return null;
					var b = a[d.data.key.children];
					i.removeChildNodes(d, a);
					return b ? b : null
				},
				removeNode: function(a, b) {
					a && (b = !! b, b && j.apply(d.callback.beforeRemove, [d.treeId, a], !0) == !1 || (i.removeNode(d, a), b && this.setting.treeObj.trigger(e.event.REMOVE, [d.treeId, a])))
				},
				selectNode: function(a, b) {
					if (a && j.uCanDo(d)) {
						b = d.view.selectedMulti && b;
						if (a.parentTId) i.expandCollapseParentNode(d, a.getParentNode(), !0, !1, function() {
							try {
								k(a, d).focus().blur()
							} catch (b) {}
						});
						else try {
							k(a, d).focus().blur()
						} catch (c) {}
						i.selectNode(d, a, b)
					}
				},
				transformTozTreeNodes: function(a) {
					return h.transformTozTreeFormat(d, a)
				},
				transformToArray: function(a) {
					return h.transformToArrayFormat(d, a)
				},
				updateNode: function(a) {
					a && k(a, d).get(0) && j.uCanDo(d) && (i.setNodeName(d, a), i.setNodeTarget(d, a), i.setNodeUrl(d, a), i.setNodeLineIcos(d, a), i.setNodeFontCss(d, a))
				}
			};
			b.treeTools = c;
			h.setZTreeTools(d, c);
			b[a] && b[a].length > 0 ? i.createNodes(d, 0, b[a]) : d.async.enable && d.async.url && d.async.url !== "" && i.asyncNode(d);
			return c
		}
	};
	var N = p.fn.zTree,
		k = j.$,
		e = N.consts
})(jQuery);

/*
 * JQuery zTree excheck 3.5.14
 * http://zTree.me/
 *
 * Copyright (c) 2010 Hunter.z
 *
 * Licensed same as jquery - MIT License
 * http://www.opensource.org/licenses/mit-license.php
 *
 * email: hunter.z@263.net
 * Date: 2013-06-28
 */
(function(m) {
	var p, q, r, o = {
		event: {
			CHECK: "ztree_check"
		},
		id: {
			CHECK: "_check"
		},
		checkbox: {
			STYLE: "checkbox",
			DEFAULT: "chk",
			DISABLED: "disable",
			FALSE: "false",
			TRUE: "true",
			FULL: "full",
			PART: "part",
			FOCUS: "focus"
		},
		radio: {
			STYLE: "radio",
			TYPE_ALL: "all",
			TYPE_LEVEL: "level"
		}
	},
		v = {
			check: {
				enable: !1,
				autoCheckTrigger: !1,
				chkStyle: o.checkbox.STYLE,
				nocheckInherit: !1,
				chkDisabledInherit: !1,
				radioType: o.radio.TYPE_LEVEL,
				chkboxType: {
					Y: "ps",
					N: "ps"
				}
			},
			data: {
				key: {
					checked: "checked"
				}
			},
			callback: {
				beforeCheck: null,
				onCheck: null
			}
		};
	p = function(b, a) {
		if (a.chkDisabled === !0) return !1;
		var c = f.getSetting(b.data.treeId),
			d = c.data.key.checked;
		if (k.apply(c.callback.beforeCheck, [c.treeId, a], !0) == !1) return !0;
		a[d] = !a[d];
		e.checkNodeRelation(c, a);
		d = n(a, j.id.CHECK, c);
		e.setChkClass(c, d, a);
		e.repairParentChkClassWithSelf(c, a);
		c.treeObj.trigger(j.event.CHECK, [b, c.treeId, a]);
		return !0
	};
	q = function(b, a) {
		if (a.chkDisabled === !0) return !1;
		var c = f.getSetting(b.data.treeId),
			d = n(a, j.id.CHECK, c);
		a.check_Focus = !0;
		e.setChkClass(c, d, a);
		return !0
	};
	r = function(b, a) {
		if (a.chkDisabled === !0) return !1;
		var c = f.getSetting(b.data.treeId),
			d = n(a, j.id.CHECK, c);
		a.check_Focus = !1;
		e.setChkClass(c, d, a);
		return !0
	};
	m.extend(!0, m.fn.zTree.consts, o);
	m.extend(!0, m.fn.zTree._z, {
		tools: {},
		view: {
			checkNodeRelation: function(b, a) {
				var c, d, h, i = b.data.key.children,
					l = b.data.key.checked;
				c = j.radio;
				if (b.check.chkStyle == c.STYLE) {
					var g = f.getRadioCheckedList(b);
					if (a[l]) if (b.check.radioType == c.TYPE_ALL) {
						for (d = g.length - 1; d >= 0; d--) c = g[d], c[l] = !1, g.splice(d, 1), e.setChkClass(b, n(c, j.id.CHECK, b), c), c.parentTId != a.parentTId && e.repairParentChkClassWithSelf(b, c);
						g.push(a)
					} else {
						g = a.parentTId ? a.getParentNode() : f.getRoot(b);
						for (d = 0, h = g[i].length; d < h; d++) c = g[i][d], c[l] && c != a && (c[l] = !1, e.setChkClass(b, n(c, j.id.CHECK, b), c))
					} else if (b.check.radioType == c.TYPE_ALL) for (d = 0, h = g.length; d < h; d++) if (a == g[d]) {
						g.splice(d, 1);
						break
					}
				} else a[l] && (!a[i] || a[i].length == 0 || b.check.chkboxType.Y.indexOf("s") > -1) && e.setSonNodeCheckBox(b, a, !0), !a[l] && (!a[i] || a[i].length == 0 || b.check.chkboxType.N.indexOf("s") > -1) && e.setSonNodeCheckBox(b, a, !1), a[l] && b.check.chkboxType.Y.indexOf("p") > -1 && e.setParentNodeCheckBox(b, a, !0), !a[l] && b.check.chkboxType.N.indexOf("p") > -1 && e.setParentNodeCheckBox(b, a, !1)
			},
			makeChkClass: function(b, a) {
				var c = b.data.key.checked,
					d = j.checkbox,
					h = j.radio,
					i = "",
					i = a.chkDisabled === !0 ? d.DISABLED : a.halfCheck ? d.PART : b.check.chkStyle == h.STYLE ? a.check_Child_State < 1 ? d.FULL : d.PART : a[c] ? a.check_Child_State === 2 || a.check_Child_State === -1 ? d.FULL : d.PART : a.check_Child_State < 1 ? d.FULL : d.PART,
					c = b.check.chkStyle + "_" + (a[c] ? d.TRUE : d.FALSE) + "_" + i,
					c = a.check_Focus && a.chkDisabled !== !0 ? c + "_" + d.FOCUS : c;
				return j.className.BUTTON + " " + d.DEFAULT + " " + c
			},
			repairAllChk: function(b, a) {
				if (b.check.enable && b.check.chkStyle === j.checkbox.STYLE) for (var c = b.data.key.checked, d = b.data.key.children, h = f.getRoot(b), i = 0, l = h[d].length; i < l; i++) {
					var g = h[d][i];
					g.nocheck !== !0 && g.chkDisabled !== !0 && (g[c] = a);
					e.setSonNodeCheckBox(b, g, a)
				}
			},
			repairChkClass: function(b, a) {
				if (a && (f.makeChkFlag(b, a), a.nocheck !== !0)) {
					var c = n(a, j.id.CHECK, b);
					e.setChkClass(b, c, a)
				}
			},
			repairParentChkClass: function(b, a) {
				if (a && a.parentTId) {
					var c = a.getParentNode();
					e.repairChkClass(b, c);
					e.repairParentChkClass(b, c)
				}
			},
			repairParentChkClassWithSelf: function(b, a) {
				if (a) {
					var c = b.data.key.children;
					a[c] && a[c].length > 0 ? e.repairParentChkClass(b, a[c][0]) : e.repairParentChkClass(b, a)
				}
			},
			repairSonChkDisabled: function(b, a, c, d) {
				if (a) {
					var h = b.data.key.children;
					if (a.chkDisabled != c) a.chkDisabled = c;
					e.repairChkClass(b, a);
					if (a[h] && d) for (var i = 0, l = a[h].length; i < l; i++) e.repairSonChkDisabled(b, a[h][i], c, d)
				}
			},
			repairParentChkDisabled: function(b, a, c, d) {
				if (a) {
					if (a.chkDisabled != c && d) a.chkDisabled = c;
					e.repairChkClass(b, a);
					e.repairParentChkDisabled(b, a.getParentNode(), c, d)
				}
			},
			setChkClass: function(b, a, c) {
				a && (c.nocheck === !0 ? a.hide() : a.show(), a.removeClass(), a.addClass(e.makeChkClass(b, c)))
			},
			setParentNodeCheckBox: function(b, a, c, d) {
				var h = b.data.key.children,
					i = b.data.key.checked,
					l = n(a, j.id.CHECK, b);
				d || (d = a);
				f.makeChkFlag(b, a);
				a.nocheck !== !0 && a.chkDisabled !== !0 && (a[i] = c, e.setChkClass(b, l, a), b.check.autoCheckTrigger && a != d && b.treeObj.trigger(j.event.CHECK, [null, b.treeId, a]));
				if (a.parentTId) {
					l = !0;
					if (!c) for (var h = a.getParentNode()[h], g = 0, k = h.length; g < k; g++) if (h[g].nocheck !== !0 && h[g].chkDisabled !== !0 && h[g][i] || (h[g].nocheck === !0 || h[g].chkDisabled === !0) && h[g].check_Child_State > 0) {
						l = !1;
						break
					}
					l && e.setParentNodeCheckBox(b, a.getParentNode(), c, d)
				}
			},
			setSonNodeCheckBox: function(b, a, c, d) {
				if (a) {
					var h = b.data.key.children,
						i = b.data.key.checked,
						l = n(a, j.id.CHECK, b);
					d || (d = a);
					var g = !1;
					if (a[h]) for (var k = 0, m = a[h].length; k < m && a.chkDisabled !== !0; k++) {
						var o = a[h][k];
						e.setSonNodeCheckBox(b, o, c, d);
						o.chkDisabled === !0 && (g = !0)
					}
					if (a != f.getRoot(b) && a.chkDisabled !== !0) {
						g && a.nocheck !== !0 && f.makeChkFlag(b, a);
						if (a.nocheck !== !0 && a.chkDisabled !== !0) {
							if (a[i] = c, !g) a.check_Child_State = a[h] && a[h].length > 0 ? c ? 2 : 0 : -1
						} else a.check_Child_State = -1;
						e.setChkClass(b, l, a);
						b.check.autoCheckTrigger && a != d && a.nocheck !== !0 && a.chkDisabled !== !0 && b.treeObj.trigger(j.event.CHECK, [null, b.treeId, a])
					}
				}
			}
		},
		event: {},
		data: {
			getRadioCheckedList: function(b) {
				for (var a = f.getRoot(b).radioCheckedList, c = 0, d = a.length; c < d; c++) f.getNodeCache(b, a[c].tId) || (a.splice(c, 1), c--, d--);
				return a
			},
			getCheckStatus: function(b, a) {
				if (!b.check.enable || a.nocheck || a.chkDisabled) return null;
				var c = b.data.key.checked;
				return {
					checked: a[c],
					half: a.halfCheck ? a.halfCheck : b.check.chkStyle == j.radio.STYLE ? a.check_Child_State === 2 : a[c] ? a.check_Child_State > -1 && a.check_Child_State < 2 : a.check_Child_State > 0
				}
			},
			getTreeCheckedNodes: function(b, a, c, d) {
				if (!a) return [];
				for (var h = b.data.key.children, i = b.data.key.checked, e = c && b.check.chkStyle == j.radio.STYLE && b.check.radioType == j.radio.TYPE_ALL, d = !d ? [] : d, g = 0, k = a.length; g < k; g++) {
					if (a[g].nocheck !== !0 && a[g].chkDisabled !== !0 && a[g][i] == c && (d.push(a[g]), e)) break;
					f.getTreeCheckedNodes(b, a[g][h], c, d);
					if (e && d.length > 0) break
				}
				return d
			},
			getTreeChangeCheckedNodes: function(b, a, c) {
				if (!a) return [];
				for (var d = b.data.key.children, h = b.data.key.checked, c = !c ? [] : c, i = 0, e = a.length; i < e; i++) a[i].nocheck !== !0 && a[i].chkDisabled !== !0 && a[i][h] != a[i].checkedOld && c.push(a[i]), f.getTreeChangeCheckedNodes(b, a[i][d], c);
				return c
			},
			makeChkFlag: function(b, a) {
				if (a) {
					var c = b.data.key.children,
						d = b.data.key.checked,
						h = -1;
					if (a[c]) for (var i = 0, e = a[c].length; i < e; i++) {
						var g = a[c][i],
							f = -1;
						if (b.check.chkStyle == j.radio.STYLE) if (f = g.nocheck === !0 || g.chkDisabled === !0 ? g.check_Child_State : g.halfCheck === !0 ? 2 : g[d] ? 2 : g.check_Child_State > 0 ? 2 : 0, f == 2) {
							h = 2;
							break
						} else f == 0 && (h = 0);
						else if (b.check.chkStyle == j.checkbox.STYLE) if (f = g.nocheck === !0 || g.chkDisabled === !0 ? g.check_Child_State : g.halfCheck === !0 ? 1 : g[d] ? g.check_Child_State === -1 || g.check_Child_State === 2 ? 2 : 1 : g.check_Child_State > 0 ? 1 : 0, f === 1) {
							h = 1;
							break
						} else if (f === 2 && h > -1 && i > 0 && f !== h) {
							h = 1;
							break
						} else if (h === 2 && f > -1 && f < 2) {
							h = 1;
							break
						} else f > -1 && (h = f)
					}
					a.check_Child_State = h
				}
			}
		}
	});
	var m = m.fn.zTree,
		k = m._z.tools,
		j = m.consts,
		e = m._z.view,
		f = m._z.data,
		n = k.$;
	f.exSetting(v);
	f.addInitBind(function(b) {
		b.treeObj.bind(j.event.CHECK, function(a, c, d, h) {
			k.apply(b.callback.onCheck, [c ? c : a, d, h])
		})
	});
	f.addInitUnBind(function(b) {
		b.treeObj.unbind(j.event.CHECK)
	});
	f.addInitCache(function() {});
	f.addInitNode(function(b, a, c, d) {
		if (c) {
			a = b.data.key.checked;
			typeof c[a] == "string" && (c[a] = k.eqs(c[a], "true"));
			c[a] = !! c[a];
			c.checkedOld = c[a];
			if (typeof c.nocheck == "string") c.nocheck = k.eqs(c.nocheck, "true");
			c.nocheck = !! c.nocheck || b.check.nocheckInherit && d && !! d.nocheck;
			if (typeof c.chkDisabled == "string") c.chkDisabled = k.eqs(c.chkDisabled, "true");
			c.chkDisabled = !! c.chkDisabled || b.check.chkDisabledInherit && d && !! d.chkDisabled;
			if (typeof c.halfCheck == "string") c.halfCheck = k.eqs(c.halfCheck, "true");
			c.halfCheck = !! c.halfCheck;
			c.check_Child_State = -1;
			c.check_Focus = !1;
			c.getCheckStatus = function() {
				return f.getCheckStatus(b, c)
			};
			b.check.chkStyle == j.radio.STYLE && b.check.radioType == j.radio.TYPE_ALL && c[a] && f.getRoot(b).radioCheckedList.push(c)
		}
	});
	f.addInitProxy(function(b) {
		var a = b.target,
			c = f.getSetting(b.data.treeId),
			d = "",
			h = null,
			e = "",
			l = null;
		if (k.eqs(b.type, "mouseover")) {
			if (c.check.enable && k.eqs(a.tagName, "span") && a.getAttribute("treeNode" + j.id.CHECK) !== null) d = k.getNodeMainDom(a).id, e = "mouseoverCheck"
		} else if (k.eqs(b.type, "mouseout")) {
			if (c.check.enable && k.eqs(a.tagName, "span") && a.getAttribute("treeNode" + j.id.CHECK) !== null) d = k.getNodeMainDom(a).id, e = "mouseoutCheck"
		} else if (k.eqs(b.type, "click") && c.check.enable && k.eqs(a.tagName, "span") && a.getAttribute("treeNode" + j.id.CHECK) !== null) d = k.getNodeMainDom(a).id, e = "checkNode";
		if (d.length > 0) switch (h = f.getNodeCache(c, d), e) {
		case "checkNode":
			l = p;
			break;
		case "mouseoverCheck":
			l = q;
			break;
		case "mouseoutCheck":
			l = r
		}
		return {
			stop: e === "checkNode",
			node: h,
			nodeEventType: e,
			nodeEventCallback: l,
			treeEventType: "",
			treeEventCallback: null
		}
	}, !0);
	f.addInitRoot(function(b) {
		f.getRoot(b).radioCheckedList = []
	});
	f.addBeforeA(function(b, a, c) {
		b.check.enable && (f.makeChkFlag(b, a), c.push("<span ID='", a.tId, j.id.CHECK, "' class='", e.makeChkClass(b, a), "' treeNode", j.id.CHECK, a.nocheck === !0 ? " style='display:none;'" : "", "></span>"))
	});
	f.addZTreeTools(function(b, a) {
		a.checkNode = function(a, c, i, f) {
			var g = b.data.key.checked;
			if (a.chkDisabled !== !0 && (c !== !0 && c !== !1 && (c = !a[g]), f = !! f, (a[g] !== c || i) && !(f && k.apply(this.setting.callback.beforeCheck, [b.treeId, a], !0) == !1) && k.uCanDo(this.setting) && b.check.enable && a.nocheck !== !0)) a[g] = c, c = n(a, j.id.CHECK, b), (i || b.check.chkStyle === j.radio.STYLE) && e.checkNodeRelation(b, a), e.setChkClass(b, c, a), e.repairParentChkClassWithSelf(b, a), f && b.treeObj.trigger(j.event.CHECK, [null, b.treeId, a])
		};
		a.checkAllNodes = function(a) {
			e.repairAllChk(b, !! a)
		};
		a.getCheckedNodes = function(a) {
			var c = b.data.key.children;
			return f.getTreeCheckedNodes(b, f.getRoot(b)[c], a !== !1)
		};
		a.getChangeCheckedNodes = function() {
			var a = b.data.key.children;
			return f.getTreeChangeCheckedNodes(b, f.getRoot(b)[a])
		};
		a.setChkDisabled = function(a, c, f, j) {
			c = !! c;
			f = !! f;
			e.repairSonChkDisabled(b, a, c, !! j);
			e.repairParentChkDisabled(b, a.getParentNode(), c, f)
		};
		var c = a.updateNode;
		a.updateNode = function(d, f) {
			c && c.apply(a, arguments);
			if (d && b.check.enable && n(d, b).get(0) && k.uCanDo(b)) {
				var i = n(d, j.id.CHECK, b);
				(f == !0 || b.check.chkStyle === j.radio.STYLE) && e.checkNodeRelation(b, d);
				e.setChkClass(b, i, d);
				e.repairParentChkClassWithSelf(b, d)
			}
		}
	});
	var s = e.createNodes;
	e.createNodes = function(b, a, c, d) {
		s && s.apply(e, arguments);
		c && e.repairParentChkClassWithSelf(b, d)
	};
	var t = e.removeNode;
	e.removeNode = function(b, a) {
		var c = a.getParentNode();
		t && t.apply(e, arguments);
		a && c && (e.repairChkClass(b, c), e.repairParentChkClass(b, c))
	};
	var u = e.appendNodes;
	e.appendNodes = function(b, a, c, d, h, i) {
		var j = "";
		u && (j = u.apply(e, arguments));
		d && f.makeChkFlag(b, d);
		return j
	}
})(jQuery);

/*
 * JQuery zTree exedit 3.5.14
 * http://zTree.me/
 *
 * Copyright (c) 2010 Hunter.z
 *
 * Licensed same as jquery - MIT License
 * http://www.opensource.org/licenses/mit-license.php
 *
 * email: hunter.z@263.net
 * Date: 2013-06-28
 */
(function(u) {
	var H = {
		event: {
			DRAG: "ztree_drag",
			DROP: "ztree_drop",
			REMOVE: "ztree_remove",
			RENAME: "ztree_rename"
		},
		id: {
			EDIT: "_edit",
			INPUT: "_input",
			REMOVE: "_remove"
		},
		move: {
			TYPE_INNER: "inner",
			TYPE_PREV: "prev",
			TYPE_NEXT: "next"
		},
		node: {
			CURSELECTED_EDIT: "curSelectedNode_Edit",
			TMPTARGET_TREE: "tmpTargetzTree",
			TMPTARGET_NODE: "tmpTargetNode"
		}
	},
		x = {
			onHoverOverNode: function(b, a) {
				var c = m.getSetting(b.data.treeId),
					d = m.getRoot(c);
				if (d.curHoverNode != a) x.onHoverOutNode(b);
				d.curHoverNode = a;
				f.addHoverDom(c, a)
			},
			onHoverOutNode: function(b) {
				var b = m.getSetting(b.data.treeId),
					a = m.getRoot(b);
				if (a.curHoverNode && !m.isSelectedNode(b, a.curHoverNode)) f.removeTreeDom(b, a.curHoverNode), a.curHoverNode = null
			},
			onMousedownNode: function(b, a) {
				function c(b) {
					if (A.dragFlag == 0 && Math.abs(M - b.clientX) < e.edit.drag.minMoveSize && Math.abs(N - b.clientY) < e.edit.drag.minMoveSize) return !0;
					var a, c, g, j, k;
					k = e.data.key.children;
					L.css("cursor", "pointer");
					if (A.dragFlag == 0) {
						if (h.apply(e.callback.beforeDrag, [e.treeId, l], !0) == !1) return n(b), !0;
						for (a = 0, c = l.length; a < c; a++) {
							if (a == 0) A.dragNodeShowBefore = [];
							g = l[a];
							g.isParent && g.open ? (f.expandCollapseNode(e, g, !g.open), A.dragNodeShowBefore[g.tId] = !0) : A.dragNodeShowBefore[g.tId] = !1
						}
						A.dragFlag = 1;
						t.showHoverDom = !1;
						h.showIfameMask(e, !0);
						g = !0;
						j = -1;
						if (l.length > 1) {
							var s = l[0].parentTId ? l[0].getParentNode()[k] : m.getNodes(e);
							k = [];
							for (a = 0, c = s.length; a < c; a++) if (A.dragNodeShowBefore[s[a].tId] !== void 0 && (g && j > -1 && j + 1 !== a && (g = !1), k.push(s[a]), j = a), l.length === k.length) {
								l = k;
								break
							}
						}
						g && (G = l[0].getPreNode(), Q = l[l.length - 1].getNextNode());
						C = o("<ul class='zTreeDragUL'></ul>", e);
						for (a = 0, c = l.length; a < c; a++) if (g = l[a], g.editNameFlag = !1, f.selectNode(e, g, a > 0), f.removeTreeDom(e, g), j = o("<li id='" + g.tId + "_tmp'></li>", e), j.append(o(g, d.id.A, e).clone()), j.css("padding", "0"), j.children("#" + g.tId + d.id.A).removeClass(d.node.CURSELECTED), C.append(j), a == e.edit.drag.maxShowNodeNum - 1) {
							j = o("<li id='" + g.tId + "_moretmp'><a>  ...  </a></li>", e);
							C.append(j);
							break
						}
						C.attr("id", l[0].tId + d.id.UL + "_tmp");
						C.addClass(e.treeObj.attr("class"));
						C.appendTo(L);
						z = o("<span class='tmpzTreeMove_arrow'></span>", e);
						z.attr("id", "zTreeMove_arrow_tmp");
						z.appendTo(L);
						e.treeObj.trigger(d.event.DRAG, [b, e.treeId, l])
					}
					if (A.dragFlag == 1) {
						r && z.attr("id") == b.target.id && v && b.clientX + E.scrollLeft() + 2 > u("#" + v + d.id.A, r).offset().left ? (g = u("#" + v + d.id.A, r), b.target = g.length > 0 ? g.get(0) : b.target) : r && (r.removeClass(d.node.TMPTARGET_TREE), v && u("#" + v + d.id.A, r).removeClass(d.node.TMPTARGET_NODE + "_" + d.move.TYPE_PREV).removeClass(d.node.TMPTARGET_NODE + "_" + H.move.TYPE_NEXT).removeClass(d.node.TMPTARGET_NODE + "_" + H.move.TYPE_INNER));
						v = r = null;
						I = !1;
						i = e;
						g = m.getSettings();
						for (var D in g) if (g[D].treeId && g[D].edit.enable && g[D].treeId != e.treeId && (b.target.id == g[D].treeId || u(b.target).parents("#" + g[D].treeId).length > 0)) I = !0, i = g[D];
						D = E.scrollTop();
						j = E.scrollLeft();
						k = i.treeObj.offset();
						a = i.treeObj.get(0).scrollHeight;
						g = i.treeObj.get(0).scrollWidth;
						c = b.clientY + D - k.top;
						var p = i.treeObj.height() + k.top - b.clientY - D,
							q = b.clientX + j - k.left,
							x = i.treeObj.width() + k.left - b.clientX - j;
						k = c < e.edit.drag.borderMax && c > e.edit.drag.borderMin;
						var s = p < e.edit.drag.borderMax && p > e.edit.drag.borderMin,
							J = q < e.edit.drag.borderMax && q > e.edit.drag.borderMin,
							F = x < e.edit.drag.borderMax && x > e.edit.drag.borderMin,
							p = c > e.edit.drag.borderMin && p > e.edit.drag.borderMin && q > e.edit.drag.borderMin && x > e.edit.drag.borderMin,
							q = k && i.treeObj.scrollTop() <= 0,
							x = s && i.treeObj.scrollTop() + i.treeObj.height() + 10 >= a,
							O = J && i.treeObj.scrollLeft() <= 0,
							P = F && i.treeObj.scrollLeft() + i.treeObj.width() + 10 >= g;
						if (b.target.id && i.treeObj.find("#" + b.target.id).length > 0) {
							for (var B = b.target; B && B.tagName && !h.eqs(B.tagName, "li") && B.id != i.treeId;) B = B.parentNode;
							var R = !0;
							for (a = 0, c = l.length; a < c; a++) if (g = l[a], B.id === g.tId) {
								R = !1;
								break
							} else if (o(g, e).find("#" + B.id).length > 0) {
								R = !1;
								break
							}
							if (R && b.target.id && (b.target.id == B.id + d.id.A || u(b.target).parents("#" + B.id + d.id.A).length > 0)) r = u(B), v = B.id
						}
						g = l[0];
						if (p && (b.target.id == i.treeId || u(b.target).parents("#" + i.treeId).length > 0)) {
							if (!r && (b.target.id == i.treeId || q || x || O || P) && (I || !I && g.parentTId)) r = i.treeObj;
							k ? i.treeObj.scrollTop(i.treeObj.scrollTop() - 10) : s && i.treeObj.scrollTop(i.treeObj.scrollTop() + 10);
							J ? i.treeObj.scrollLeft(i.treeObj.scrollLeft() - 10) : F && i.treeObj.scrollLeft(i.treeObj.scrollLeft() + 10);
							r && r != i.treeObj && r.offset().left < i.treeObj.offset().left && i.treeObj.scrollLeft(i.treeObj.scrollLeft() + r.offset().left - i.treeObj.offset().left)
						}
						C.css({
							top: b.clientY + D + 3 + "px",
							left: b.clientX + j + 3 + "px"
						});
						k = a = 0;
						if (r && r.attr("id") != i.treeId) {
							var y = v == null ? null : m.getNodeCache(i, v);
							c = b.ctrlKey && e.edit.drag.isMove && e.edit.drag.isCopy || !e.edit.drag.isMove && e.edit.drag.isCopy;
							a = !! (G && v === G.tId);
							k = !! (Q && v === Q.tId);
							j = g.parentTId && g.parentTId == v;
							g = (c || !k) && h.apply(i.edit.drag.prev, [i.treeId, l, y], !! i.edit.drag.prev);
							a = (c || !a) && h.apply(i.edit.drag.next, [i.treeId, l, y], !! i.edit.drag.next);
							F = (c || !j) && !(i.data.keep.leaf && !y.isParent) && h.apply(i.edit.drag.inner, [i.treeId, l, y], !! i.edit.drag.inner);
							if (!g && !a && !F) {
								if (r = null, v = "", w = d.move.TYPE_INNER, z.css({
									display: "none"
								}), window.zTreeMoveTimer) clearTimeout(window.zTreeMoveTimer), window.zTreeMoveTargetNodeTId = null
							} else {
								c = u("#" + v + d.id.A, r);
								k = y.isLastNode ? null : u("#" + y.getNextNode().tId + d.id.A, r.next());
								s = c.offset().top;
								j = c.offset().left;
								J = g ? F ? 0.25 : a ? 0.5 : 1 : -1;
								F = a ? F ? 0.75 : g ? 0.5 : 0 : -1;
								b = (b.clientY + D - s) / c.height();
								(J == 1 || b <= J && b >= -0.2) && g ? (a = 1 - z.width(), k = s - z.height() / 2, w = d.move.TYPE_PREV) : (F == 0 || b >= F && b <= 1.2) && a ? (a = 1 - z.width(), k = k == null || y.isParent && y.open ? s + c.height() - z.height() / 2 : k.offset().top - z.height() / 2, w = d.move.TYPE_NEXT) : (a = 5 - z.width(), k = s, w = d.move.TYPE_INNER);
								z.css({
									display: "block",
									top: k + "px",
									left: j + a + "px"
								});
								c.addClass(d.node.TMPTARGET_NODE + "_" + w);
								if (S != v || T != w) K = (new Date).getTime();
								if (y && y.isParent && w == d.move.TYPE_INNER && (b = !0, window.zTreeMoveTimer && window.zTreeMoveTargetNodeTId !== y.tId ? (clearTimeout(window.zTreeMoveTimer), window.zTreeMoveTargetNodeTId = null) : window.zTreeMoveTimer && window.zTreeMoveTargetNodeTId === y.tId && (b = !1), b)) window.zTreeMoveTimer = setTimeout(function() {
									w == d.move.TYPE_INNER && y && y.isParent && !y.open && (new Date).getTime() - K > i.edit.drag.autoOpenTime && h.apply(i.callback.beforeDragOpen, [i.treeId, y], !0) && (f.switchNode(i, y), i.edit.drag.autoExpandTrigger && i.treeObj.trigger(d.event.EXPAND, [i.treeId, y]))
								}, i.edit.drag.autoOpenTime + 50), window.zTreeMoveTargetNodeTId = y.tId
							}
						} else if (w = d.move.TYPE_INNER, r && h.apply(i.edit.drag.inner, [i.treeId, l, null], !! i.edit.drag.inner) ? r.addClass(d.node.TMPTARGET_TREE) : r = null, z.css({
							display: "none"
						}), window.zTreeMoveTimer) clearTimeout(window.zTreeMoveTimer), window.zTreeMoveTargetNodeTId = null;
						S = v;
						T = w
					}
					return !1
				}
				function n(b) {
					if (window.zTreeMoveTimer) clearTimeout(window.zTreeMoveTimer), window.zTreeMoveTargetNodeTId = null;
					T = S = null;
					E.unbind("mousemove", c);
					E.unbind("mouseup", n);
					E.unbind("selectstart", g);
					L.css("cursor", "auto");
					r && (r.removeClass(d.node.TMPTARGET_TREE), v && u("#" + v + d.id.A, r).removeClass(d.node.TMPTARGET_NODE + "_" + d.move.TYPE_PREV).removeClass(d.node.TMPTARGET_NODE + "_" + H.move.TYPE_NEXT).removeClass(d.node.TMPTARGET_NODE + "_" + H.move.TYPE_INNER));
					h.showIfameMask(e, !1);
					t.showHoverDom = !0;
					if (A.dragFlag != 0) {
						A.dragFlag = 0;
						var a, k, j;
						for (a = 0, k = l.length; a < k; a++) j = l[a], j.isParent && A.dragNodeShowBefore[j.tId] && !j.open && (f.expandCollapseNode(e, j, !j.open), delete A.dragNodeShowBefore[j.tId]);
						C && C.remove();
						z && z.remove();
						var p = b.ctrlKey && e.edit.drag.isMove && e.edit.drag.isCopy || !e.edit.drag.isMove && e.edit.drag.isCopy;
						!p && r && v && l[0].parentTId && v == l[0].parentTId && w == d.move.TYPE_INNER && (r = null);
						if (r) {
							var q = v == null ? null : m.getNodeCache(i, v);
							if (h.apply(e.callback.beforeDrop, [i.treeId, l, q, w, p], !0) == !1) f.selectNodes(x, l);
							else {
								var s = p ? h.clone(l) : l;
								a = function() {
									if (I) {
										if (!p) for (var a = 0, c = l.length; a < c; a++) f.removeNode(e, l[a]);
										if (w == d.move.TYPE_INNER) f.addNodes(i, q, s);
										else if (f.addNodes(i, q.getParentNode(), s), w == d.move.TYPE_PREV) for (a = 0, c = s.length; a < c; a++) f.moveNode(i, q, s[a], w, !1);
										else for (a = -1, c = s.length - 1; a < c; c--) f.moveNode(i, q, s[c], w, !1)
									} else if (p && w == d.move.TYPE_INNER) f.addNodes(i, q, s);
									else if (p && f.addNodes(i, q.getParentNode(), s), w != d.move.TYPE_NEXT) for (a = 0, c = s.length; a < c; a++) f.moveNode(i, q, s[a], w, !1);
									else for (a = -1, c = s.length - 1; a < c; c--) f.moveNode(i, q, s[c], w, !1);
									f.selectNodes(i, s);
									o(s[0], e).focus().blur();
									e.treeObj.trigger(d.event.DROP, [b, i.treeId, s, q, w, p])
								};
								w == d.move.TYPE_INNER && h.canAsync(i, q) ? f.asyncNode(i, q, !1, a) : a()
							}
						} else f.selectNodes(x, l), e.treeObj.trigger(d.event.DROP, [b, e.treeId, l, null, null, null])
					}
				}
				function g() {
					return !1
				}
				var k, j, e = m.getSetting(b.data.treeId),
					A = m.getRoot(e),
					t = m.getRoots();
				if (b.button == 2 || !e.edit.enable || !e.edit.drag.isCopy && !e.edit.drag.isMove) return !0;
				var p = b.target,
					q = m.getRoot(e).curSelectedList,
					l = [];
				if (m.isSelectedNode(e, a)) for (k = 0, j = q.length; k < j; k++) {
					if (q[k].editNameFlag && h.eqs(p.tagName, "input") && p.getAttribute("treeNode" + d.id.INPUT) !== null) return !0;
					l.push(q[k]);
					if (l[0].parentTId !== q[k].parentTId) {
						l = [a];
						break
					}
				} else l = [a];
				f.editNodeBlur = !0;
				f.cancelCurEditNode(e);
				var E = u(e.treeObj.get(0).ownerDocument),
					L = u(e.treeObj.get(0).ownerDocument.body),
					C, z, r, I = !1,
					i = e,
					x = e,
					G, Q, S = null,
					T = null,
					v = null,
					w = d.move.TYPE_INNER,
					M = b.clientX,
					N = b.clientY,
					K = (new Date).getTime();
				h.uCanDo(e) && E.bind("mousemove", c);
				E.bind("mouseup", n);
				E.bind("selectstart", g);
				b.preventDefault && b.preventDefault();
				return !0
			}
		};
	u.extend(!0, u.fn.zTree.consts, H);
	u.extend(!0, u.fn.zTree._z, {
		tools: {
			getAbs: function(b) {
				b = b.getBoundingClientRect();
				return [b.left + (document.body.scrollLeft + document.documentElement.scrollLeft), b.top + (document.body.scrollTop + document.documentElement.scrollTop)]
			},
			inputFocus: function(b) {
				b.get(0) && (b.focus(), h.setCursorPosition(b.get(0), b.val().length))
			},
			inputSelect: function(b) {
				b.get(0) && (b.focus(), b.select())
			},
			setCursorPosition: function(b, a) {
				if (b.setSelectionRange) b.focus(), b.setSelectionRange(a, a);
				else if (b.createTextRange) {
					var c = b.createTextRange();
					c.collapse(!0);
					c.moveEnd("character", a);
					c.moveStart("character", a);
					c.select()
				}
			},
			showIfameMask: function(b, a) {
				for (var c = m.getRoot(b); c.dragMaskList.length > 0;) c.dragMaskList[0].remove(), c.dragMaskList.shift();
				if (a) for (var d = o("iframe", b), g = 0, f = d.length; g < f; g++) {
					var j = d.get(g),
						e = h.getAbs(j),
						j = o("<div id='zTreeMask_" + g + "' class='zTreeMask' style='top:" + e[1] + "px; left:" + e[0] + "px; width:" + j.offsetWidth + "px; height:" + j.offsetHeight + "px;'></div>", b);
					j.appendTo(o("body", b));
					c.dragMaskList.push(j)
				}
			}
		},
		view: {
			addEditBtn: function(b, a) {
				if (!(a.editNameFlag || o(a, d.id.EDIT, b).length > 0) && h.apply(b.edit.showRenameBtn, [b.treeId, a], b.edit.showRenameBtn)) {
					var c = o(a, d.id.A, b),
						n = "<span class='" + d.className.BUTTON + " edit' id='" + a.tId + d.id.EDIT + "' title='" + h.apply(b.edit.renameTitle, [b.treeId, a], b.edit.renameTitle) + "' treeNode" + d.id.EDIT + " style='display:none;'></span>";
					c.append(n);
					o(a, d.id.EDIT, b).bind("click", function() {
						if (!h.uCanDo(b) || h.apply(b.callback.beforeEditName, [b.treeId, a], !0) == !1) return !1;
						f.editNode(b, a);
						return !1
					}).show()
				}
			},
			addRemoveBtn: function(b, a) {
				if (!(a.editNameFlag || o(a, d.id.REMOVE, b).length > 0) && h.apply(b.edit.showRemoveBtn, [b.treeId, a], b.edit.showRemoveBtn)) {
					var c = o(a, d.id.A, b),
						n = "<span class='" + d.className.BUTTON + " remove' id='" + a.tId + d.id.REMOVE + "' title='" + h.apply(b.edit.removeTitle, [b.treeId, a], b.edit.removeTitle) + "' treeNode" + d.id.REMOVE + " style='display:none;'></span>";
					c.append(n);
					o(a, d.id.REMOVE, b).bind("click", function() {
						if (!h.uCanDo(b) || h.apply(b.callback.beforeRemove, [b.treeId, a], !0) == !1) return !1;
						f.removeNode(b, a);
						b.treeObj.trigger(d.event.REMOVE, [b.treeId, a]);
						return !1
					}).bind("mousedown", function() {
						return !0
					}).show()
				}
			},
			addHoverDom: function(b, a) {
				if (m.getRoots().showHoverDom) a.isHover = !0, b.edit.enable && (f.addEditBtn(b, a), f.addRemoveBtn(b, a)), h.apply(b.view.addHoverDom, [b.treeId, a])
			},
			cancelCurEditNode: function(b, a) {
				var c = m.getRoot(b),
					n = b.data.key.name,
					g = c.curEditNode;
				if (g) {
					var k = c.curEditInput,
						j = a ? a : k.val(),
						e = !! a;
					if (h.apply(b.callback.beforeRename, [b.treeId, g, j, e], !0) === !1) return !1;
					else g[n] = j ? j : k.val(), b.treeObj.trigger(d.event.RENAME, [b.treeId, g, e]);
					o(g, d.id.A, b).removeClass(d.node.CURSELECTED_EDIT);
					k.unbind();
					f.setNodeName(b, g);
					g.editNameFlag = !1;
					c.curEditNode = null;
					c.curEditInput = null;
					f.selectNode(b, g, !1)
				}
				return c.noSelection = !0
			},
			editNode: function(b, a) {
				var c = m.getRoot(b);
				f.editNodeBlur = !1;
				if (m.isSelectedNode(b, a) && c.curEditNode == a && a.editNameFlag) setTimeout(function() {
					h.inputFocus(c.curEditInput)
				}, 0);
				else {
					var n = b.data.key.name;
					a.editNameFlag = !0;
					f.removeTreeDom(b, a);
					f.cancelCurEditNode(b);
					f.selectNode(b, a, !1);
					o(a, d.id.SPAN, b).html("<input type=text class='rename' id='" + a.tId + d.id.INPUT + "' treeNode" + d.id.INPUT + " >");
					var g = o(a, d.id.INPUT, b);
					g.attr("value", a[n]);
					b.edit.editNameSelectAll ? h.inputSelect(g) : h.inputFocus(g);
					g.bind("blur", function() {
						f.editNodeBlur || f.cancelCurEditNode(b)
					}).bind("keydown", function(c) {
						c.keyCode == "13" ? (f.editNodeBlur = !0, f.cancelCurEditNode(b)) : c.keyCode == "27" && f.cancelCurEditNode(b, a[n])
					}).bind("click", function() {
						return !1
					}).bind("dblclick", function() {
						return !1
					});
					o(a, d.id.A, b).addClass(d.node.CURSELECTED_EDIT);
					c.curEditInput = g;
					c.noSelection = !1;
					c.curEditNode = a
				}
			},
			moveNode: function(b, a, c, n, g, k) {
				var j = m.getRoot(b),
					e = b.data.key.children;
				if (a != c && (!b.data.keep.leaf || !a || a.isParent || n != d.move.TYPE_INNER)) {
					var h = c.parentTId ? c.getParentNode() : j,
						t = a === null || a == j;
					t && a === null && (a = j);
					if (t) n = d.move.TYPE_INNER;
					j = a.parentTId ? a.getParentNode() : j;
					if (n != d.move.TYPE_PREV && n != d.move.TYPE_NEXT) n = d.move.TYPE_INNER;
					if (n == d.move.TYPE_INNER) if (t) c.parentTId = null;
					else {
						if (!a.isParent) a.isParent = !0, a.open = !! a.open, f.setNodeLineIcos(b, a);
						c.parentTId = a.tId
					}
					var p;
					t ? p = t = b.treeObj : (!k && n == d.move.TYPE_INNER ? f.expandCollapseNode(b, a, !0, !1) : k || f.expandCollapseNode(b, a.getParentNode(), !0, !1), t = o(a, b), p = o(a, d.id.UL, b), t.get(0) && !p.get(0) && (p = [], f.makeUlHtml(b, a, p, ""), t.append(p.join(""))), p = o(a, d.id.UL, b));
					var q = o(c, b);
					q.get(0) ? t.get(0) || q.remove() : q = f.appendNodes(b, c.level, [c], null, !1, !0).join("");
					p.get(0) && n == d.move.TYPE_INNER ? p.append(q) : t.get(0) && n == d.move.TYPE_PREV ? t.before(q) : t.get(0) && n == d.move.TYPE_NEXT && t.after(q);
					var l = -1,
						u = 0,
						x = null,
						t = null,
						C = c.level;
					if (c.isFirstNode) {
						if (l = 0, h[e].length > 1) x = h[e][1], x.isFirstNode = !0
					} else if (c.isLastNode) l = h[e].length - 1, x = h[e][l - 1], x.isLastNode = !0;
					else for (p = 0, q = h[e].length; p < q; p++) if (h[e][p].tId == c.tId) {
						l = p;
						break
					}
					l >= 0 && h[e].splice(l, 1);
					if (n != d.move.TYPE_INNER) for (p = 0, q = j[e].length; p < q; p++) j[e][p].tId == a.tId && (u = p);
					if (n == d.move.TYPE_INNER) {
						a[e] || (a[e] = []);
						if (a[e].length > 0) t = a[e][a[e].length - 1], t.isLastNode = !1;
						a[e].splice(a[e].length, 0, c);
						c.isLastNode = !0;
						c.isFirstNode = a[e].length == 1
					} else a.isFirstNode && n == d.move.TYPE_PREV ? (j[e].splice(u, 0, c), t = a, t.isFirstNode = !1, c.parentTId = a.parentTId, c.isFirstNode = !0, c.isLastNode = !1) : a.isLastNode && n == d.move.TYPE_NEXT ? (j[e].splice(u + 1, 0, c), t = a, t.isLastNode = !1, c.parentTId = a.parentTId, c.isFirstNode = !1, c.isLastNode = !0) : (n == d.move.TYPE_PREV ? j[e].splice(u, 0, c) : j[e].splice(u + 1, 0, c), c.parentTId = a.parentTId, c.isFirstNode = !1, c.isLastNode = !1);
					m.fixPIdKeyValue(b, c);
					m.setSonNodeLevel(b, c.getParentNode(), c);
					f.setNodeLineIcos(b, c);
					f.repairNodeLevelClass(b, c, C);
					!b.data.keep.parent && h[e].length < 1 ? (h.isParent = !1, h.open = !1, a = o(h, d.id.UL, b), n = o(h, d.id.SWITCH, b), e = o(h, d.id.ICON, b), f.replaceSwitchClass(h, n, d.folder.DOCU), f.replaceIcoClass(h, e, d.folder.DOCU), a.css("display", "none")) : x && f.setNodeLineIcos(b, x);
					t && f.setNodeLineIcos(b, t);
					b.check && b.check.enable && f.repairChkClass && (f.repairChkClass(b, h), f.repairParentChkClassWithSelf(b, h), h != c.parent && f.repairParentChkClassWithSelf(b, c));
					k || f.expandCollapseParentNode(b, c.getParentNode(), !0, g)
				}
			},
			removeEditBtn: function(b, a) {
				o(a, d.id.EDIT, b).unbind().remove()
			},
			removeRemoveBtn: function(b, a) {
				o(a, d.id.REMOVE, b).unbind().remove()
			},
			removeTreeDom: function(b, a) {
				a.isHover = !1;
				f.removeEditBtn(b, a);
				f.removeRemoveBtn(b, a);
				h.apply(b.view.removeHoverDom, [b.treeId, a])
			},
			repairNodeLevelClass: function(b, a, c) {
				if (c !== a.level) {
					var f = o(a, b),
						g = o(a, d.id.A, b),
						b = o(a, d.id.UL, b),
						c = d.className.LEVEL + c,
						a = d.className.LEVEL + a.level;
					f.removeClass(c);
					f.addClass(a);
					g.removeClass(c);
					g.addClass(a);
					b.removeClass(c);
					b.addClass(a)
				}
			},
			selectNodes: function(b, a) {
				for (var c = 0, d = a.length; c < d; c++) f.selectNode(b, a[c], c > 0)
			}
		},
		event: {},
		data: {
			setSonNodeLevel: function(b, a, c) {
				if (c) {
					var d = b.data.key.children;
					c.level = a ? a.level + 1 : 0;
					if (c[d]) for (var a = 0, g = c[d].length; a < g; a++) c[d][a] && m.setSonNodeLevel(b, c, c[d][a])
				}
			}
		}
	});
	var G = u.fn.zTree,
		h = G._z.tools,
		d = G.consts,
		f = G._z.view,
		m = G._z.data,
		o = h.$;
	m.exSetting({
		edit: {
			enable: !1,
			editNameSelectAll: !1,
			showRemoveBtn: !0,
			showRenameBtn: !0,
			removeTitle: "remove",
			renameTitle: "rename",
			drag: {
				autoExpandTrigger: !1,
				isCopy: !0,
				isMove: !0,
				prev: !0,
				next: !0,
				inner: !0,
				minMoveSize: 5,
				borderMax: 10,
				borderMin: -5,
				maxShowNodeNum: 5,
				autoOpenTime: 500
			}
		},
		view: {
			addHoverDom: null,
			removeHoverDom: null
		},
		callback: {
			beforeDrag: null,
			beforeDragOpen: null,
			beforeDrop: null,
			beforeEditName: null,
			beforeRename: null,
			onDrag: null,
			onDrop: null,
			onRename: null
		}
	});
	m.addInitBind(function(b) {
		var a = b.treeObj,
			c = d.event;
		a.bind(c.RENAME, function(a, c, d, f) {
			h.apply(b.callback.onRename, [a, c, d, f])
		});
		a.bind(c.REMOVE, function(a, c, d) {
			h.apply(b.callback.onRemove, [a, c, d])
		});
		a.bind(c.DRAG, function(a, c, d, f) {
			h.apply(b.callback.onDrag, [c, d, f])
		});
		a.bind(c.DROP, function(a, c, d, f, e, m, o) {
			h.apply(b.callback.onDrop, [c, d, f, e, m, o])
		})
	});
	m.addInitUnBind(function(b) {
		var b = b.treeObj,
			a = d.event;
		b.unbind(a.RENAME);
		b.unbind(a.REMOVE);
		b.unbind(a.DRAG);
		b.unbind(a.DROP)
	});
	m.addInitCache(function() {});
	m.addInitNode(function(b, a, c) {
		if (c) c.isHover = !1, c.editNameFlag = !1
	});
	m.addInitProxy(function(b) {
		var a = b.target,
			c = m.getSetting(b.data.treeId),
			f = b.relatedTarget,
			g = "",
			k = null,
			j = "",
			e = null,
			o = null;
		if (h.eqs(b.type, "mouseover")) {
			if (o = h.getMDom(c, a, [{
				tagName: "a",
				attrName: "treeNode" + d.id.A
			}])) g = h.getNodeMainDom(o).id, j = "hoverOverNode"
		} else if (h.eqs(b.type, "mouseout")) o = h.getMDom(c, f, [{
			tagName: "a",
			attrName: "treeNode" + d.id.A
		}]), o || (g = "remove", j = "hoverOutNode");
		else if (h.eqs(b.type, "mousedown") && (o = h.getMDom(c, a, [{
			tagName: "a",
			attrName: "treeNode" + d.id.A
		}]))) g = h.getNodeMainDom(o).id, j = "mousedownNode";
		if (g.length > 0) switch (k = m.getNodeCache(c, g), j) {
		case "mousedownNode":
			e = x.onMousedownNode;
			break;
		case "hoverOverNode":
			e = x.onHoverOverNode;
			break;
		case "hoverOutNode":
			e = x.onHoverOutNode
		}
		return {
			stop: !1,
			node: k,
			nodeEventType: j,
			nodeEventCallback: e,
			treeEventType: "",
			treeEventCallback: null
		}
	});
	m.addInitRoot(function(b) {
		var b = m.getRoot(b),
			a = m.getRoots();
		b.curEditNode = null;
		b.curEditInput = null;
		b.curHoverNode = null;
		b.dragFlag = 0;
		b.dragNodeShowBefore = [];
		b.dragMaskList = [];
		a.showHoverDom = !0
	});
	m.addZTreeTools(function(b, a) {
		a.cancelEditName = function(a) {
			var d = m.getRoot(b),
				g = b.data.key.name,
				h = d.curEditNode;
			d.curEditNode && f.cancelCurEditNode(b, a ? a : h[g])
		};
		a.copyNode = function(a, n, g, k) {
			if (!n) return null;
			if (a && !a.isParent && b.data.keep.leaf && g === d.move.TYPE_INNER) return null;
			var j = h.clone(n);
			if (!a) a = null, g = d.move.TYPE_INNER;
			g == d.move.TYPE_INNER ? (n = function() {
				f.addNodes(b, a, [j], k)
			}, h.canAsync(b, a) ? f.asyncNode(b, a, k, n) : n()) : (f.addNodes(b, a.parentNode, [j], k), f.moveNode(b, a, j, g, !1, k));
			return j
		};
		a.editName = function(a) {
			a && a.tId && a === m.getNodeCache(b, a.tId) && (a.parentTId && f.expandCollapseParentNode(b, a.getParentNode(), !0), f.editNode(b, a))
		};
		a.moveNode = function(a, n, g, k) {
			function j() {
				f.moveNode(b, a, n, g, !1, k)
			}
			if (!n) return n;
			if (a && !a.isParent && b.data.keep.leaf && g === d.move.TYPE_INNER) return null;
			else if (a && (n.parentTId == a.tId && g == d.move.TYPE_INNER || o(n, b).find("#" + a.tId).length > 0)) return null;
			else a || (a = null);
			h.canAsync(b, a) && g === d.move.TYPE_INNER ? f.asyncNode(b, a, k, j) : j();
			return n
		};
		a.setEditable = function(a) {
			b.edit.enable = a;
			return this.refresh()
		}
	});
	var M = f.cancelPreSelectedNode;
	f.cancelPreSelectedNode = function(b, a) {
		for (var c = m.getRoot(b).curSelectedList, d = 0, g = c.length; d < g; d++) if (!a || a === c[d]) if (f.removeTreeDom(b, c[d]), a) break;
		M && M.apply(f, arguments)
	};
	var N = f.createNodes;
	f.createNodes = function(b, a, c, d) {
		N && N.apply(f, arguments);
		c && f.repairParentChkClassWithSelf && f.repairParentChkClassWithSelf(b, d)
	};
	var U = f.makeNodeUrl;
	f.makeNodeUrl = function(b, a) {
		return b.edit.enable ? null : U.apply(f, arguments)
	};
	var K = f.removeNode;
	f.removeNode = function(b, a) {
		var c = m.getRoot(b);
		if (c.curEditNode === a) c.curEditNode = null;
		K && K.apply(f, arguments)
	};
	var O = f.selectNode;
	f.selectNode = function(b, a, c) {
		var d = m.getRoot(b);
		if (m.isSelectedNode(b, a) && d.curEditNode == a && a.editNameFlag) return !1;
		O && O.apply(f, arguments);
		f.addHoverDom(b, a);
		return !0
	};
	var P = h.uCanDo;
	h.uCanDo = function(b, a) {
		var c = m.getRoot(b);
		return a && (h.eqs(a.type, "mouseover") || h.eqs(a.type, "mouseout") || h.eqs(a.type, "mousedown") || h.eqs(a.type, "mouseup")) ? !0 : !c.curEditNode && (P ? P.apply(f, arguments) : !0)
	}
})(jQuery);