/* =========================================================
   Gestor de Inventario
   - Listas multinivel: tipo -> marca -> modelo -> productos
   - Tabla con filtros
   - Exportación CSV, HTML y XML
   - Persistencia opcional en localStorage
   ========================================================= */

const STORAGE_KEY = "inventario_multinivel_v1";

/** Estructura multinivel:
 * inventory = [
 *  {
 *    type: "Laptop",
 *    brands: [
 *      {
 *        name: "Dell",
 *        models: [
 *          {
 *            name: "Inspiron 15",
 *            products: [
 *              { id, name, qty, price, desc, createdAt }
 *            ]
 *          }
 *        ]
 *      }
 *    ]
 *  }
 * ]
 */
let inventory = loadInventory();

/* ---------- DOM ---------- */
const form = document.getElementById("productForm");

const typeInput = document.getElementById("typeInput");
const brandInput = document.getElementById("brandInput");
const modelInput = document.getElementById("modelInput");
const nameInput = document.getElementById("nameInput");
const qtyInput = document.getElementById("qtyInput");
const priceInput = document.getElementById("priceInput");
const descInput = document.getElementById("descInput");

const typeFilter = document.getElementById("typeFilter");
const brandFilter = document.getElementById("brandFilter");
const modelFilter = document.getElementById("modelFilter");
const searchInput = document.getElementById("searchInput");

const tableBody = document.getElementById("tableBody");
const stats = document.getElementById("stats");

const exportCsvBtn = document.getElementById("exportCsvBtn");
const exportHtmlBtn = document.getElementById("exportHtmlBtn");
const exportXmlBtn = document.getElementById("exportXmlBtn");

const seedBtn = document.getElementById("seedBtn");
const clearBtn = document.getElementById("clearBtn");

/* ---------- Init ---------- */
refreshFilters();
renderTable();

/* ---------- Events ---------- */
form.addEventListener("submit", (e) => {
  e.preventDefault();
  addProduct({
    type: typeInput.value.trim(),
    brand: brandInput.value.trim(),
    model: modelInput.value.trim(),
    name: nameInput.value.trim(),
    qty: Number(qtyInput.value),
    price: Number(priceInput.value),
    desc: descInput.value.trim()
  });
  form.reset();
  qtyInput.value = 1;
  priceInput.value = 0;
});

typeFilter.addEventListener("change", () => {
  refreshFilters();
  renderTable();
});
brandFilter.addEventListener("change", () => {
  refreshFilters(); // para cascada de modelos
  renderTable();
});
modelFilter.addEventListener("change", renderTable);
searchInput.addEventListener("input", renderTable);

exportCsvBtn.addEventListener("click", () => exportCSV(getFilteredFlatRows()));
exportHtmlBtn.addEventListener("click", () => exportHTML(getFilteredFlatRows()));
exportXmlBtn.addEventListener("click", () => exportXML(getFilteredFlatRows()));

seedBtn.addEventListener("click", seedExample);
clearBtn.addEventListener("click", () => {
  if (!confirm("¿Seguro que quieres vaciar el inventario?")) return;
  inventory = [];
  persist();
  refreshFilters();
  renderTable();
});

/* =========================================================
   CRUD / Multinivel
   ========================================================= */

function addProduct({ type, brand, model, name, qty, price, desc }) {
  if (!type || !brand || !model || !name) return;

  const product = {
    id: crypto.randomUUID(),
    name,
    qty,
    price,
    desc: desc || "",
    createdAt: new Date().toISOString()
  };

  // Buscar o crear tipo
  let typeNode = inventory.find(t => equalsIgnoreCase(t.type, type));
  if (!typeNode) {
    typeNode = { type, brands: [] };
    inventory.push(typeNode);
  }

  // Buscar o crear marca dentro del tipo
  let brandNode = typeNode.brands.find(b => equalsIgnoreCase(b.name, brand));
  if (!brandNode) {
    brandNode = { name: brand, models: [] };
    typeNode.brands.push(brandNode);
  }

  // Buscar o crear modelo dentro de marca
  let modelNode = brandNode.models.find(m => equalsIgnoreCase(m.name, model));
  if (!modelNode) {
    modelNode = { name: model, products: [] };
    brandNode.models.push(modelNode);
  }

  modelNode.products.push(product);

  persist();
  refreshFilters();
  renderTable();
}

function deleteProduct(id) {
  for (const t of inventory) {
    for (const b of t.brands) {
      for (const m of b.models) {
        const idx = m.products.findIndex(p => p.id === id);
        if (idx !== -1) {
          m.products.splice(idx, 1);
        }
      }
      // limpiar modelos vacíos
      b.models = b.models.filter(m => m.products.length > 0);
    }
    // limpiar marcas vacías
    t.brands = t.brands.filter(b => b.models.length > 0);
  }
  // limpiar tipos vacíos
  inventory = inventory.filter(t => t.brands.length > 0);

  persist();
  refreshFilters();
  renderTable();
}

/* =========================================================
   Flatten + filtros
   ========================================================= */

function flattenInventory() {
  const rows = [];
  for (const t of inventory) {
    for (const b of t.brands) {
      for (const m of b.models) {
        for (const p of m.products) {
          rows.push({
            type: t.type,
            brand: b.name,
            model: m.name,
            ...p
          });
        }
      }
    }
  }
  return rows;
}

function getFilteredFlatRows() {
  const selectedType = typeFilter.value;
  const selectedBrand = brandFilter.value;
  const selectedModel = modelFilter.value;
  const q = searchInput.value.trim().toLowerCase();

  let rows = flattenInventory();

  if (selectedType) rows = rows.filter(r => r.type === selectedType);
  if (selectedBrand) rows = rows.filter(r => r.brand === selectedBrand);
  if (selectedModel) rows = rows.filter(r => r.model === selectedModel);

  if (q) {
    rows = rows.filter(r =>
      r.name.toLowerCase().includes(q) ||
      (r.desc || "").toLowerCase().includes(q)
    );
  }

  return rows;
}

/* =========================================================
   UI
   ========================================================= */

function refreshFilters() {
  // Tipos
  const types = inventory.map(t => t.type).sort();
  fillSelect(typeFilter, types, "Todos");

  // Marcas (dependen de tipo)
  const selectedType = typeFilter.value;
  let brands = [];
  if (selectedType) {
    const t = inventory.find(x => x.type === selectedType);
    brands = (t?.brands || []).map(b => b.name).sort();
    brandFilter.disabled = false;
  } else {
    // si no hay tipo seleccionado se deshabilita cascada
    brands = [];
    brandFilter.disabled = true;
  }
  fillSelect(brandFilter, brands, "Todas");

  // Modelos (dependen de marca)
  const selectedBrand = brandFilter.value;
  let models = [];
  if (selectedType && selectedBrand) {
    const t = inventory.find(x => x.type === selectedType);
    const b = t?.brands.find(x => x.name === selectedBrand);
    models = (b?.models || []).map(m => m.name).sort();
    modelFilter.disabled = false;
  } else {
    models = [];
    modelFilter.disabled = true;
  }
  fillSelect(modelFilter, models, "Todos");
}

function fillSelect(select, values, placeholder) {
  const current = select.value;
  select.innerHTML = `<option value="">${placeholder}</option>`;
  for (const v of values) {
    const opt = document.createElement("option");
    opt.value = v;
    opt.textContent = v;
    select.appendChild(opt);
  }
  // intenta mantener selección
  if (values.includes(current)) select.value = current;
}

function renderTable() {
  const rows = getFilteredFlatRows();

  tableBody.innerHTML = "";
  if (rows.length === 0) {
    const tr = document.createElement("tr");
    tr.innerHTML = `<td colspan="8" style="color:var(--muted); padding:16px;">
      No hay productos para mostrar.
    </td>`;
    tableBody.appendChild(tr);
    stats.textContent = "0 productos";
    return;
  }

  let totalItems = 0;
  let totalCost = 0;

  for (const r of rows) {
    const rowTotal = r.qty * r.price;
    totalItems += r.qty;
    totalCost += rowTotal;

    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td><span class="tag">${escapeHTML(r.type)}</span></td>
      <td>${escapeHTML(r.brand)}</td>
      <td>${escapeHTML(r.model)}</td>
      <td>
        <div><strong>${escapeHTML(r.name)}</strong></div>
        ${r.desc ? `<div class="muted">${escapeHTML(r.desc)}</div>` : ""}
      </td>
      <td class="qty">${r.qty}</td>
      <td class="price">$${r.price.toFixed(2)}</td>
      <td class="total">$${rowTotal.toFixed(2)}</td>
      <td>
        <button class="btn danger" data-del="${r.id}">Eliminar</button>
      </td>
    `;
    tableBody.appendChild(tr);
  }

  tableBody.querySelectorAll("[data-del]").forEach(btn => {
    btn.addEventListener("click", () => deleteProduct(btn.dataset.del));
  });

  stats.textContent =
    `${rows.length} productos · ${totalItems} unidades · $${totalCost.toFixed(2)} total`;
}

/* =========================================================
   Exportación
   ========================================================= */

function exportCSV(rows) {
  const header = ["Tipo","Marca","Modelo","Producto","Cantidad","Precio","Total","Descripción"];
  const lines = [header.join(",")];

  for (const r of rows) {
    const line = [
      r.type, r.brand, r.model, r.name,
      r.qty, r.price.toFixed(2),
      (r.qty * r.price).toFixed(2),
      r.desc || ""
    ].map(csvEscape).join(",");
    lines.push(line);
  }

  downloadFile(lines.join("\n"), "inventario.csv", "text/csv;charset=utf-8;");
}

function exportHTML(rows) {
  const html = `<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<title>Inventario Exportado</title>
<style>
  body{ font-family: Arial, sans-serif; padding:16px;}
  table{ width:100%; border-collapse: collapse;}
  th,td{ border:1px solid #ddd; padding:8px; text-align:left;}
  th{ background:#f3f3f3;}
</style>
</head>
<body>
<h2>Inventario Exportado</h2>
<table>
  <thead>
    <tr>
      <th>Tipo</th><th>Marca</th><th>Modelo</th><th>Producto</th>
      <th>Cant.</th><th>Precio</th><th>Total</th><th>Descripción</th>
    </tr>
  </thead>
  <tbody>
    ${rows.map(r => `
      <tr>
        <td>${escapeHTML(r.type)}</td>
        <td>${escapeHTML(r.brand)}</td>
        <td>${escapeHTML(r.model)}</td>
        <td>${escapeHTML(r.name)}</td>
        <td>${r.qty}</td>
        <td>$${r.price.toFixed(2)}</td>
        <td>$${(r.qty*r.price).toFixed(2)}</td>
        <td>${escapeHTML(r.desc||"")}</td>
      </tr>
    `).join("")}
  </tbody>
</table>
</body></html>`;

  downloadFile(html, "inventario.html", "text/html;charset=utf-8;");
}

function exportXML(rows) {
  // agrupar a multinivel para respetar: tipo -> marca -> modelo
  const grouped = groupRowsMultilevel(rows);

  let xml = `<?xml version="1.0" encoding="UTF-8"?>\n<inventario>\n`;
  for (const t of grouped) {
    xml += `  <tipo nombre="${xmlEscape(t.type)}">\n`;
    for (const b of t.brands) {
      xml += `    <marca nombre="${xmlEscape(b.name)}">\n`;
      for (const m of b.models) {
        xml += `      <modelo nombre="${xmlEscape(m.name)}">\n`;
        for (const p of m.products) {
          xml += `        <producto id="${p.id}">\n`;
          xml += `          <nombre>${xmlEscape(p.name)}</nombre>\n`;
          xml += `          <cantidad>${p.qty}</cantidad>\n`;
          xml += `          <precio>${p.price.toFixed(2)}</precio>\n`;
          xml += `          <total>${(p.qty*p.price).toFixed(2)}</total>\n`;
          if (p.desc) xml += `          <descripcion>${xmlEscape(p.desc)}</descripcion>\n`;
          xml += `        </producto>\n`;
        }
        xml += `      </modelo>\n`;
      }
      xml += `    </marca>\n`;
    }
    xml += `  </tipo>\n`;
  }
  xml += `</inventario>`;

  downloadFile(xml, "inventario.xml", "application/xml;charset=utf-8;");
}

function groupRowsMultilevel(rows) {
  const map = new Map();

  for (const r of rows) {
    if (!map.has(r.type)) map.set(r.type, new Map());
    const brandMap = map.get(r.type);
    if (!brandMap.has(r.brand)) brandMap.set(r.brand, new Map());
    const modelMap = brandMap.get(r.brand);
    if (!modelMap.has(r.model)) modelMap.set(r.model, []);
    modelMap.get(r.model).push(r);
  }

  const result = [];
  for (const [type, brandMap] of map.entries()) {
    const brands = [];
    for (const [brand, modelMap] of brandMap.entries()) {
      const models = [];
      for (const [model, products] of modelMap.entries()) {
        models.push({
          name: model,
          products: products.map(p => ({
            id: p.id,
            name: p.name,
            qty: p.qty,
            price: p.price,
            desc: p.desc || ""
          }))
        });
      }
      brands.push({ name: brand, models });
    }
    result.push({ type, brands });
  }
  return result.sort((a,b)=>a.type.localeCompare(b.type));
}

/* =========================================================
   Persistencia localStorage
   ========================================================= */
function persist() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(inventory));
}

function loadInventory() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

/* =========================================================
   Utils
   ========================================================= */

function downloadFile(content, filename, mime) {
  const blob = new Blob([content], { type: mime });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

function csvEscape(value) {
  const s = String(value ?? "");
  if (/[",\n]/.test(s)) return `"${s.replace(/"/g, '""')}"`;
  return s;
}

function escapeHTML(str) {
  return String(str).replace(/[&<>"']/g, m =>
    ({ "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;" }[m])
  );
}

function xmlEscape(str) {
  return String(str).replace(/[<>&'"]/g, m =>
    ({ "<":"&lt;",">":"&gt;","&":"&amp;","'":"&apos;",'"':"&quot;" }[m])
  );
}

function equalsIgnoreCase(a, b){
  return a.toLowerCase() === b.toLowerCase();
}

/* =========================================================
   Datos de ejemplo
   ========================================================= */
function seedExample() {
  const examples = [
    {type:"Laptop", brand:"Dell", model:"Inspiron 15", name:"Inspiron 15 i5 / 16GB", qty:3, price:650, desc:"SSD 512GB"},
    {type:"Laptop", brand:"HP", model:"Pavilion", name:"Pavilion Ryzen 5", qty:2, price:720, desc:"14 pulgadas"},
    {type:"Periférico", brand:"Logitech", model:"G Pro", name:"Mouse G Pro Wireless", qty:5, price:95, desc:"Gaming"},
    {type:"Periférico", brand:"Razer", model:"BlackWidow", name:"Teclado BlackWidow V3", qty:4, price:110, desc:"Mecánico"},
    {type:"Suplemento", brand:"Optimum Nutrition", model:"Gold Standard", name:"Whey 5lb", qty:6, price:78, desc:"Vainilla"}
  ];
  for (const e of examples) addProduct(e);
}
